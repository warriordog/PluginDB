package net.acomputerdog.plugindb.db;

import net.acomputerdog.plugindb.query.GetQuery;
import net.acomputerdog.plugindb.query.RawQuery;
import net.acomputerdog.plugindb.query.SetQuery;
import net.acomputerdog.plugindb.schema.Table;

public interface Database {
    void connect();

    void disconnect();

    void createTable(Table table);

    void handleRawQuery(RawQuery query);
    void handleGetQuery(GetQuery query);
    void handleSetQuery(SetQuery query);
}
