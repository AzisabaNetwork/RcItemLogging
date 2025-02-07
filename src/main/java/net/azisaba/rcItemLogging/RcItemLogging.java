package net.azisaba.rcItemLogging;

import net.azisaba.rcItemLogging.logging.PlayerLogManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class RcItemLogging extends JavaPlugin {
    private PlayerLogManager playerLogManager;

    @Override
    public void onEnable() {
        getLogger().info("Initializing...");
        var dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        getLogger().info("Data folder path: " + getDataPath());
        playerLogManager = new PlayerLogManager(getLogger(), dataFolder);

        // log test code
//        playerLogManager.put(
//                "welcome",
//                "minecraft:stone",
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                null
//        );

        getLogger().info("Initialized!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling now...");
        playerLogManager.closeAll();
        getLogger().info("See you!");
    }
}
