package net.azisaba.rcItemLogging;

import net.azisaba.rcItemLogging.logging.PlayerLogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;
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
            @NotNull UUID playerFrom,
            @NotNull UUID playerTo,
            @Nullable String additionalMsg
    ) {
        if(!isInitialized()) throw new RuntimeException("Log manager wasn't initialized!");

        playerLogManager.put(eventType, itemData, playerFrom, playerTo, additionalMsg);
    }

    static void close() {
        playerLogManager.closeAll();
    }
}
