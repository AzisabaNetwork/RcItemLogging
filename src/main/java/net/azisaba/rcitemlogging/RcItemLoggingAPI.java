package net.azisaba.rcitemlogging;

import net.azisaba.rcitemlogging.manager.PlayerLogManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

public final class RcItemLoggingAPI {
    private static PlayerLogManager playerLogManager;
    static void init(Logger logger, File logFolderPath) {
        playerLogManager = new PlayerLogManager(logger, logFolderPath);
    }

    public static boolean isInitialized() {
        return playerLogManager != null;
    }

    public static void put(
            @NotNull String eventType,
            @NotNull String itemData,
            @NotNull OfflinePlayer playerFrom,
            @NotNull OfflinePlayer playerTo,
            @Nullable String additionalMsg
    ) {
        if(!isInitialized()) throw new RuntimeException("Log manager wasn't initialized!");

        playerLogManager.put(eventType, itemData, playerFrom, playerTo, additionalMsg);
    }

    public static void putSystem(
            @NotNull String eventType,
            @NotNull String itemData,
            @NotNull String playerFrom,
            @NotNull OfflinePlayer playerTo,
            @Nullable String additionalMsg
    ) {
        if(!isInitialized()) throw new RuntimeException("Log manager wasn't initialized!");

        playerLogManager.putSystem(eventType, itemData, playerFrom, playerTo, additionalMsg);
    }

    public static void putSystem(
            @NotNull String eventType,
            @NotNull String itemData,
            @NotNull OfflinePlayer playerFrom,
            @NotNull String playerTo,
            @Nullable String additionalMsg
    ) {
        if(!isInitialized()) throw new RuntimeException("Log manager wasn't initialized!");

        playerLogManager.putSystem(eventType, itemData, playerFrom, playerTo, additionalMsg);
    }

    static void close() {
        playerLogManager.closeAll();
    }
}
