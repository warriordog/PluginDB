package net.acomputerdog.plugindb.query;

import java.sql.ResultSet;

public interface QueryCallback {
    void onQueryComplete(ResultSet results);
}
