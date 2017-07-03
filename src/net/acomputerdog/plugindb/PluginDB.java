package net.acomputerdog.plugindb;

import net.acomputerdog.plugindb.db.Database;
import net.acomputerdog.plugindb.ex.PDBException;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginDB {
    private final JavaPlugin plugin;

    /*
        Internal state
     */
    private boolean isLoaded = false;
    private boolean isConnected = false;
    private DBSettings settings;
    private int tickEventID = -1;

    private Database db;

    public PluginDB(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        // load with defaults
        load(new DBSettings());
    }

    public void load(DBSettings settings) {
        if (isConnected) {
            throw new IllegalStateException("load() called after connect()");
        }
        this.settings = settings;

        // if semi-sync is enabled, then we need a tick handler
        if (settings.isSemiSyncEnabled()) {
            tickEventID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::onTick, 0, 1);
            if (tickEventID == -1) {
                throw new PDBException("Unable to schedule task handler for tick updates.");
            }
        }

        // create the database
        if (settings.getDatabaseProvider() != null) {
            db = settings.getDatabaseProvider().createDatabase(this);
            if (db == null) {
                throw new IllegalArgumentException("Database provider returned null");
            }
        } else {
            throw new IllegalArgumentException("Database provider is null");
        }

        isLoaded = true;
    }

    public void connect() {
        if (!isConnected) {
            if (isLoaded) {
                try {
                    // lock settings to prevent changes
                    settings.lock();

                    // connect to database
                    db.connect();

                    isConnected = true;
                } catch (Exception e) {
                    throw new PDBException("Exception while connecting to database.", e);
                }
            } else {
                throw new IllegalStateException("connect() called before load()");
            }
        }
    }

    public void disconnect() {
        if (isConnected) {
            try {
                isConnected = false;

                // disconnect from database
                db.disconnect();
            } catch (Exception e) {
                throw new PDBException("Exception while disconnecting from database.", e);
            }
        }
    }

    private void onTick() {

    }

    public DBSettings getSettings() {
        return settings;
    }
}
