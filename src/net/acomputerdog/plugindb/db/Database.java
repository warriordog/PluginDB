package net.acomputerdog.plugindb.db;

import net.acomputerdog.plugindb.schema.Table;

public interface Database {
    void connect();

    void disconnect();

    void createTable(Table table);
}
