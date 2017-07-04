package net.acomputerdog.plugindb.db.hsqldb;

import net.acomputerdog.concurrent.LockedNotifier;
import net.acomputerdog.plugindb.PluginDB;
import net.acomputerdog.plugindb.db.Database;
import net.acomputerdog.plugindb.ex.DatabaseException;
import net.acomputerdog.plugindb.ex.QueryFormatException;
import net.acomputerdog.plugindb.query.*;
import net.acomputerdog.plugindb.schema.Column;
import net.acomputerdog.plugindb.schema.Table;
import net.acomputerdog.plugindb.schema.field.FType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class HSQLDBDatabase implements Database {
    private final PluginDB pdb;

    // Database connection
    private Connection conn;

    // database executor thread
    private Thread dbThread;
    private final Queue<Runnable> queryQueue = new LinkedList<>();
    private final Semaphore queryQueueSemaphore = new Semaphore(1);
    private final LockedNotifier queryQueueNotifier = new LockedNotifier();

    public HSQLDBDatabase(PluginDB pdb) {
        this.pdb = pdb;
    }

    @Override
    public void connect() {
        try {
            // this will cause static initialization of HSQLDB
            Class.forName("org.hsqldb.jdbcDriver");

            // database path
            String dbPath = "jdbc:hsqldb:" + pdb.getSettings().getDatabasePath();

            // SA is the default username
            conn = DriverManager.getConnection(dbPath, "SA", "");

            dbThread = new Thread(() -> {
                try {
                    while (true) {
                        // wait for a query (returns false if interrupted
                        if (queryQueueNotifier.waitForNotify()) {
                            queryQueueSemaphore.acquireUninterruptibly();

                            // get a query
                            Runnable q = null;
                            try {
                                // don't use while, because notifier maintains internal count
                                if (!queryQueue.isEmpty()) {
                                    q = queryQueue.poll();
                                }
                            } finally {
                                queryQueueSemaphore.release();
                            }

                            // can be null only if the notifier gets out of sync (which would be a bug)
                            if (q != null) {
                                q.run();
                            }
                        }
                    }
                } catch (Exception e) {
                    pdb.shutdownOnError("Exception in db thread", e);
                }
            });
            dbThread.setDaemon(true);
            dbThread.start();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("HSQLDB is missing, please add it to the classpath.", e);
        } catch (SQLException e) {
            throw new DatabaseException("Unable to open or create database", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Exception closing database.");
        }
    }

    @Override
    public void createTable(Table table) {
        try {
            StringBuilder query = new StringBuilder();
            // base statement
            query.append("CREATE CACHED TABLE IF NOT EXISTS ");
            query.append(table.getName());

            // fields
            query.append('(');
            for (int i = 0; i < table.getNumColumns(); i++) {
                if (i > 0) {
                    query.append(", ");
                }
                Column c = table.getColumnIdx(i);
                query.append(c.getName());
                query.append(' ');
                query.append(getSQLType(c.getType(), c.getTypeMod()));
                if (c.getTypeMod() != null) {
                    query.append('(');
                    query.append(c.getTypeMod());
                    query.append(')');
                }
                if (!c.isNullable()) {
                    query.append(" NOT NULL");
                }
            }

            // constraints
            for (int i = 0; i < table.getNumColumns(); i++) {
                Column c = table.getColumnIdx(i);
                if (c.isPrimaryKey()) {
                    query.append(", PRIMARY KEY (");
                    query.append(c.getName());
                    query.append(')');
                }
                if (c.getForeignKey() != null) {
                    query.append(", PRIMARY KEY (");
                    query.append(c.getName());
                    query.append(") REFERENCES ");
                    query.append(c.getForeignKey());
                }
            }

            // ending paren
            query.append(')');

            int res = conn.createStatement().executeUpdate(query.toString());
            if (res == -1) {
                throw new QueryFormatException("Error creating table.");
            }
        } catch (SQLException e) {
            throw new QueryFormatException("SQL error while creating table", e);
        }
    }

    private void runStatement(Query query) {
        try {
            query.getStatement().execute();
        } catch (SQLException e) {
            throw new QueryFormatException("Error in SQL query", e);
        }
    }

    private ResultSet runQuery(Query query) {
        try {
            return query.getStatement().executeQuery();
        } catch (SQLException e) {
            throw new QueryFormatException("Error in SQL query", e);
        }
    }

    private int runUpdate(Query query) {
        try {
            return query.getStatement().executeUpdate();
        } catch (SQLException e) {
            throw new QueryFormatException("Error in SQL query", e);
        }
    }

    private void queueTask(Runnable r) {
        queryQueueSemaphore.acquireUninterruptibly();
        try {
            queryQueue.add(r);
            queryQueueNotifier.release();
        } finally {
            queryQueueSemaphore.release();
        }
    }

    @Override
    public void execute(Query query, Callback callback) {
        switch (query.getSyncMode()) {
            case SYNC:
                runStatement(query);
                callback.onComplete();
                break;
            case SEMI_SYNC:
                if (query.getSyncMode() == SyncMode.SEMI_SYNC && !pdb.getSettings().isSemiSyncEnabled()) {
                    throw new IllegalArgumentException("Semi-Sync mode is not enabled.");
                }
                queueTask(() -> {
                    runStatement(query);
                    pdb.getCallbackManager().addCallback(callback);
                });
                break;
            case ASYNC:
                queueTask(() -> {
                    runStatement(query);
                    callback.onComplete();
                });
                break;
            default:
                throw new IllegalArgumentException("Unknown callback mode");
        }
    }

    @Override
    public void executeQuery(Query query, QueryCallback callback) {
        switch (query.getSyncMode()) {
            case SYNC:
                callback.onQueryComplete(runQuery(query));
                break;
            case SEMI_SYNC:
                if (query.getSyncMode() == SyncMode.SEMI_SYNC && !pdb.getSettings().isSemiSyncEnabled()) {
                    throw new IllegalArgumentException("Semi-Sync mode is not enabled.");
                }
                queueTask(() -> {
                    ResultSet res = runQuery(query);
                    pdb.getCallbackManager().addQueryCallback(callback, res);
                });
                break;
            case ASYNC:
                queueTask(() -> callback.onQueryComplete(runQuery(query)));
                break;
            default:
                throw new IllegalArgumentException("Unknown callback mode");
        }
    }

    @Override
    public void executeUpdate(Query query, UpdateCallback callback) {
        switch (query.getSyncMode()) {
            case SYNC:
                callback.onUpdateComplete(runUpdate(query));
                break;
            case SEMI_SYNC:
                if (query.getSyncMode() == SyncMode.SEMI_SYNC && !pdb.getSettings().isSemiSyncEnabled()) {
                    throw new IllegalArgumentException("Semi-Sync mode is not enabled.");
                }
                queueTask(() -> {
                    int res = runUpdate(query);
                    pdb.getCallbackManager().addUpdateCallback(callback, res);
                });
                break;
            case ASYNC:
                queueTask(() -> callback.onUpdateComplete(runUpdate(query)));
                break;
            default:
                throw new IllegalArgumentException("Unknown callback mode");
        }
    }

    private String getSQLType(FType type, String typeMod) {
        switch (type) {
            case INT:
                return "INTEGER";
            case IDENTITY:
                return "INTEGER IDENTITY";
            case FLOAT:
                return "FLOAT";
            case STRING:
                if (typeMod == null) {
                    throw new QueryFormatException("STRING type requires typemod");
                }
                return "CHAR(" + typeMod + ")";
            case VARSTRING:
                if (typeMod == null) {
                    throw new QueryFormatException("STRING type requires typemod");
                }
                return "VARCHAR(" + typeMod + ")";
            default:
                throw new QueryFormatException("Unknown field type: " + type);
        }
    }
}
