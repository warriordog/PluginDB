package net.acomputerdog.plugindb.db;

import net.acomputerdog.plugindb.callback.Callback;
import net.acomputerdog.plugindb.callback.QueryCallback;
import net.acomputerdog.plugindb.callback.UpdateCallback;
import net.acomputerdog.plugindb.schema.Table;

import java.sql.PreparedStatement;

public interface Database {
    void connect();

    void disconnect();

    void createTable(Table table);

    void execute(PreparedStatement statement, Callback callback);
    void executeQuery(PreparedStatement statement, QueryCallback callback);
    void executeUpdate(PreparedStatement statement, UpdateCallback callback);
}
