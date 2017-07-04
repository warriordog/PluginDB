package net.acomputerdog.plugindb;

import net.acomputerdog.plugindb.db.Database;
import net.acomputerdog.plugindb.ex.PDBException;
import net.acomputerdog.plugindb.util.CallbackManager;
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
    private CallbackManager callbackManager;

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

        this.callbackManager = new CallbackManager();

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
        callbackManager.onTick();
    }

    public void shutdownOnError(String message, Throwable t) {
        plugin.getLogger().severe(message);
        if (t != null) {
            t.printStackTrace();
        }
        disconnect();
    }

    public Database getDatabase() {
        return db;
    }

    public DBSettings getSettings() {
        return settings;
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
}
