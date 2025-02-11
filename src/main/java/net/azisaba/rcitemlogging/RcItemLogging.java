package net.azisaba.rcitemlogging;

import net.azisaba.rcitemlogging.api.RILApi;
import net.azisaba.rcitemlogging.manager.McIdManager;
import net.azisaba.rcitemlogging.manager.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class RcItemLogging extends JavaPlugin {
    private static RILApi API_INSTANCE;
    public static RILApi getApi() {
        return API_INSTANCE;
    }

    private LogManager logManager;
    private McIdManager mcIdManager;

    public LogManager getLogManager() {
        return logManager;
    }

    @Override
    public void onEnable() {
        getLogger().info("Initializing...");

        // Setup data folder
        var dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            if(!dataFolder.mkdirs()) {
                getLogger().warning("Failed to create directory. path:" + dataFolder.getAbsolutePath());
            }
        }

        getLogger().info("Data folder path: " + getDataPath());
        logManager = new LogManager(getLogger(), dataFolder);

        mcIdManager = new McIdManager(getLogger(), logManager, new File(dataFolder, "mcid.txt"));

        // Register listeners
        Bukkit.getPluginManager().registerEvents(
                mcIdManager,
                this
        );

        // API Initialize
        API_INSTANCE = new RILApi(this);

        getLogger().info("Initialized!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling now...");
        logManager.closeAll();
        mcIdManager.save();
        getLogger().info("See you!");
    }
}
