package net.acomputerdog.plugindb.db.hsqldb;

import net.acomputerdog.plugindb.PluginDB;
import net.acomputerdog.plugindb.db.Database;
import net.acomputerdog.plugindb.schema.Table;

public class HSQLDBDatabase implements Database {
    private final PluginDB pdb;

    public HSQLDBDatabase(PluginDB pdb) {
        this.pdb = pdb;
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void createTable(Table table) {

    }
}
