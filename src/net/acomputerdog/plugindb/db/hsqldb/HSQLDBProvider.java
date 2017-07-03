package net.acomputerdog.plugindb.db.hsqldb;

import net.acomputerdog.plugindb.PluginDB;
import net.acomputerdog.plugindb.db.DBProvider;
import net.acomputerdog.plugindb.db.Database;

public class HSQLDBProvider implements DBProvider {
    @Override
    public Database createDatabase(PluginDB pdb) {
        return new HSQLDBDatabase(pdb);
    }
}
