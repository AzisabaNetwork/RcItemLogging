package net.azisaba.rcitemlogging;

import org.bukkit.plugin.java.JavaPlugin;

public final class RcItemLogging extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Initializing...");
        var dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            if(!dataFolder.mkdirs()) {
                getLogger().warning("Failed to create directory. path:" + dataFolder.getAbsolutePath());
            }
        }

        getLogger().info("Data folder path: " + getDataPath());
        RcItemLoggingAPI.init(getLogger(), dataFolder);

        getLogger().info("Initialized!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling now...");
        RcItemLoggingAPI.close();
        getLogger().info("See you!");
    }
}
