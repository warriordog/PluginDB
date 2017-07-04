package net.acomputerdog.plugindb.callback;

import java.sql.ResultSet;

public interface QueryCallback {
    void onQueryComplete(ResultSet results);
}
