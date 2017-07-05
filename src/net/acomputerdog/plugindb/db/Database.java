package net.acomputerdog.plugindb.db;

import net.acomputerdog.plugindb.query.Callback;
import net.acomputerdog.plugindb.query.Query;
import net.acomputerdog.plugindb.query.QueryCallback;
import net.acomputerdog.plugindb.query.UpdateCallback;
import net.acomputerdog.plugindb.schema.Schema;
import net.acomputerdog.plugindb.schema.Table;

import java.sql.PreparedStatement;

public interface Database {
    void connect();

    void disconnect();

    void createTable(Table table);
    default void createSchema(Schema schema) {
        for (Table t : schema.getTables()) {
            createTable(t);
        }
    }

    void execute(Query query, Callback callback);
    void executeQuery(Query query, QueryCallback callback);
    void executeUpdate(Query query, UpdateCallback callback);

    PreparedStatement prepareStatement(String query);
}
