package net.acomputerdog.plugindb;

import net.acomputerdog.plugindb.db.DBProvider;
import net.acomputerdog.plugindb.db.hsqldb.HSQLDBProvider;

public class DBSettings {
    private boolean isLocked = false;

    // If false, semi-synchronous callbacks will be disabled
    private boolean semiSyncEnabled = true;

    // Database provider to use
    private DBProvider databaseProvider = new HSQLDBProvider();

    public boolean isSemiSyncEnabled() {
        return semiSyncEnabled;
    }

    public void setSemiSyncEnabled(boolean semiSyncEnabled) {
        checkLocked();
        this.semiSyncEnabled = semiSyncEnabled;
    }

    public DBProvider getDatabaseProvider() {
        return databaseProvider;
    }

    public void setDatabaseProvider(DBProvider databaseProvider) {
        checkLocked();
        this.databaseProvider = databaseProvider;
    }

    private void checkLocked() {
        if (isLocked) {
            throw new IllegalStateException("Settings are locked.");
        }
    }

    public void lock() {
        isLocked = true;
    }
}
