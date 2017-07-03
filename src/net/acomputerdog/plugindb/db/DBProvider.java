package net.acomputerdog.plugindb.db;

import net.acomputerdog.plugindb.PluginDB;

public interface DBProvider {
    Database createDatabase(PluginDB pdb);
}
