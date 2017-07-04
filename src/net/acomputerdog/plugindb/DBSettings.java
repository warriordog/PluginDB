package net.acomputerdog.plugindb;

import net.acomputerdog.plugindb.db.DBProvider;
import net.acomputerdog.plugindb.db.hsqldb.HSQLDBProvider;

public class DBSettings {
    private boolean isLocked = false;

    // If false, semi-synchronous callbacks will be disabled
    private boolean semiSyncEnabled = true;

    // Database provider to use
    private DBProvider databaseProvider = new HSQLDBProvider();

    // relative path to database
    private String databasePath = "database/db";

    // if true, database will be automatically built from "db.cfg" in the jar
    private boolean autoBuildDB = false;

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

    public String getDatabasePath() {
        checkLocked();
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public boolean autoBuildDB() {
        return autoBuildDB;
    }

    public void setAutoBuildDB(boolean autoBuildDB) {
        this.autoBuildDB = autoBuildDB;
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
