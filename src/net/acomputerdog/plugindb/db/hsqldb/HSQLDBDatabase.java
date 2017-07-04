package net.acomputerdog.plugindb.db.hsqldb;

import net.acomputerdog.plugindb.PluginDB;
import net.acomputerdog.plugindb.db.Database;
import net.acomputerdog.plugindb.ex.DatabaseException;
import net.acomputerdog.plugindb.ex.QueryFormatException;
import net.acomputerdog.plugindb.ex.QuerySQLException;
import net.acomputerdog.plugindb.query.GetQuery;
import net.acomputerdog.plugindb.query.Query;
import net.acomputerdog.plugindb.query.RawQuery;
import net.acomputerdog.plugindb.query.SetQuery;
import net.acomputerdog.plugindb.schema.Column;
import net.acomputerdog.plugindb.schema.Table;
import net.acomputerdog.plugindb.schema.field.FType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HSQLDBDatabase implements Database {
    private final PluginDB pdb;

    // shared instance to avoid creating extra objects
    private final HSQLDBResult sharedResult;

    // Database connection
    private Connection conn;

    public HSQLDBDatabase(PluginDB pdb) {
        this.pdb = pdb;
        this.sharedResult = new HSQLDBResult();
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
    public void handleRawQuery(RawQuery query) {
        try {
            if (query.getStoredQuery() == null) {
                query.setStoredQuery(conn.prepareStatement(query.getQuery()));
            }
            handleQuery(query);
        } catch (SQLException e) {
            throw new QueryFormatException("Error in query SQL", e);
        }
    }

    @Override
    public void handleGetQuery(GetQuery query) {
        try {
            if (query.getStoredQuery() == null) {
                query.setStoredQuery(conn.prepareStatement(buildGetQuery(query)));
            }
            handleQuery(query);
        } catch (SQLException e) {
            throw new QueryFormatException("Error in query SQL", e);
        }
    }

    @Override
    public void handleSetQuery(SetQuery query) {
        try {
            if (query.getStoredQuery() == null) {
                query.setStoredQuery(conn.prepareStatement(buildSetQuery(query)));
            }

            //TODO set parameters

            handleQuery(query);
        } catch (SQLException e) {
            throw new QueryFormatException("Error in query SQL", e);
        }
    }

    private void handleQuery(Query query) {
        try {
            PreparedStatement statement = (PreparedStatement)query.getStoredQuery();
            if (query.isGet()) {
                sharedResult.setQuery(statement.executeQuery());
            } else {
                sharedResult.setUpdate(statement.executeUpdate());
            }
        } catch (ClassCastException e) {
            throw new QueryFormatException("query has wrong format of prepared statement", e);
        } catch (SQLException e) {
            throw new QuerySQLException("Exception occurred while executing SQL", e);
        }
    }

    private String buildGetQuery(GetQuery query) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");

        // append columns to get
        if (query.getWantedColumns().length == 0) {
            builder.append('*');
        } else {
            Column[] wantedColumns = query.getWantedColumns();
            for (int i = 0; i < wantedColumns.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                Column c = wantedColumns[i];
                builder.append(c.getName());
            }
        }

        builder.append(" FROM ");
        builder.append(query.getTable().getName());

        // conditions
        if (query.getConditions() != null) {
            builder.append(" WHERE ");
            builder.append(query.getConditions());
        }

        return builder.toString();
    }


    private String buildSetQuery(SetQuery query) {
        /*
            MERGE INTO t
            USING
                (VALUES(1, 'a'))
                AS v(a, b)
            ON t.a = v.a
            WHEN MATCHED THEN
                UPDATE SET t.b = v.b
            WHEN NOT MATCHED THEN
                INSERT VALUES v.a, v.b
         */
        StringBuilder builder = new StringBuilder();
        builder.append("MERGE INTO ");
        builder.append(query.getTable().getName());
        builder.append(" USING (VALUES(");

        // append values
        for (int i = 0; i < query.getNumFields(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append("?");
        }

        // name values
        builder.append(") AS v(");
        for (int i = 0; i < query.getNumFields(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append((char)('a' + i));
        }

        builder.append(" ON ");
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
