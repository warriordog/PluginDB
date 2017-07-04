package net.acomputerdog.plugindb.query;

import java.sql.PreparedStatement;

public class Query {
    private final PreparedStatement statement;
    private SyncMode syncMode;

    public Query(PreparedStatement statement, SyncMode syncMode) {
        this.statement = statement;
        this.syncMode = syncMode;
    }

    public PreparedStatement getStatement() {
        return statement;
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }
}
