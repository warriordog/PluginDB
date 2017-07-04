package net.acomputerdog.plugindb.db.hsqldb;

import net.acomputerdog.plugindb.PluginDB;
import net.acomputerdog.plugindb.callback.Callback;
import net.acomputerdog.plugindb.callback.QueryCallback;
import net.acomputerdog.plugindb.callback.UpdateCallback;
import net.acomputerdog.plugindb.db.Database;
import net.acomputerdog.plugindb.ex.DatabaseException;
import net.acomputerdog.plugindb.ex.QueryFormatException;
import net.acomputerdog.plugindb.schema.Column;
import net.acomputerdog.plugindb.schema.Table;
import net.acomputerdog.plugindb.schema.field.FType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HSQLDBDatabase implements Database {
    private final PluginDB pdb;

    // Database connection
    private Connection conn;

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
                    query.append(c.getForeignKey().getTable().getName());
                    query.append('(');
                    query.append(c.getForeignKey().getName());
                    query.append(')');
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

    @Override
    public void execute(PreparedStatement statement, Callback callback) {
        try {
            statement.execute();
            callback.onComplete();
        } catch (SQLException e) {
            throw new QueryFormatException("Error in SQL query", e);
        }
    }

    @Override
    public void executeQuery(PreparedStatement statement, QueryCallback callback) {
        try {
            callback.onQueryComplete(statement.executeQuery());
        } catch (SQLException e) {
            throw new QueryFormatException("Error in SQL query", e);
        }
    }

    @Override
    public void executeUpdate(PreparedStatement statement, UpdateCallback callback) {
        try {
            callback.onUpdateComplete(statement.executeUpdate());
        } catch (SQLException e) {
            throw new QueryFormatException("Error in SQL query", e);
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
