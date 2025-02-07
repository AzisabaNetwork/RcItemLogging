package net.azisaba.rcItemLogging;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class RcItemLogging extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Initializing...");
        var dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

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
