package net.acomputerdog.plugindb.db.hsqldb;

import java.sql.ResultSet;

public class HSQLDBResult {
    private boolean isUpdate;
    private int numAffectedRows;
    private ResultSet results;

    private void set(boolean isUpdate, int numAffectedRows, ResultSet results) {
        this.isUpdate = isUpdate;
        this.numAffectedRows = numAffectedRows;
        this.results = results;
    }

    public void setUpdate(int numAffectedRows) {
        set(true, numAffectedRows, null);
    }

    public void setQuery(ResultSet results) {
        set(false, -1, results);
    }
}
