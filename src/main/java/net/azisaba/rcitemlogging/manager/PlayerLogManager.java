package net.azisaba.rcitemlogging.manager;

import net.azisaba.rcitemlogging.logging.LogDateFormats;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.logging.Logger;

public final class PlayerLogManager {
    private final HashMap<UUID, FileWriter> logStreamMap = new HashMap<>();

    private final Logger logger;
    private final File logFolderPath;
    private final McIdManager mcIdManager;

    public PlayerLogManager(Logger logger, File parentFolderPath) {
        this.logger = logger;
        this.logFolderPath = new File(parentFolderPath, "logs");
        if(!this.logFolderPath.exists()) {
            if(!this.logFolderPath.mkdirs()) {
                this.logger.warning("Failed to create directory. path:" + logFolderPath.getAbsolutePath());
            }
        }
        this.mcIdManager = new McIdManager(logger, new File(parentFolderPath, "mcids.txt"));
    }

    private void openPlayerLog(UUID targetUuid) {
        File targetFile = new File(logFolderPath, targetUuid + ".log");
        try {
            FileWriter writer = new FileWriter(targetFile);
            logStreamMap.put(targetUuid, writer);
        } catch (Exception e) {
            logger.severe("Failed to open player log writer: " + e);
        }
    }

    public void put(
            @NotNull String eventType,
            @NotNull String itemData,
            @NotNull OfflinePlayer playerFrom,
            @NotNull OfflinePlayer playerTo,
            @Nullable String additionalMsg
    ) {
        String _msg = createLogLine(eventType, itemData, playerFrom.getName(), playerTo.getName(), additionalMsg);
        _putSingle(playerFrom, _msg);
        _putSingle(playerTo, _msg);
    }

    public void putSystem(
            @NotNull String eventType,
            @NotNull String itemData,
            @NotNull String playerFrom,
            @NotNull OfflinePlayer playerTo,
            @Nullable String additionalMsg
    ) {
        String _msg = createLogLine(eventType, itemData, playerFrom, playerTo.getName(), additionalMsg);
        _putSingle(playerTo, _msg);
    }

    public void putSystem(
            @NotNull String eventType,
            @NotNull String itemData,
            @NotNull OfflinePlayer playerFrom,
            @NotNull String playerTo,
            @Nullable String additionalMsg
    ) {
        String _msg = createLogLine(eventType, itemData, playerFrom.getName(), playerTo, additionalMsg);
        _putSingle(playerFrom, _msg);
    }

    private void _putSingle(OfflinePlayer player, String line) {
        var targetUuid = player.getUniqueId();
        if(!logStreamMap.containsKey(targetUuid)) {
            openPlayerLog(targetUuid);
        }


        var writer = logStreamMap.get(targetUuid);
        try {
            String changes = mcIdManager.getMcIdChanges(player);
            if(changes != null) writer.write(changes + System.lineSeparator());
            writer.write(line + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            logger.severe("Failed to write log data: " + e);
            logger.info("Fallback log: " + line);

            try {
                writer.close();
            } catch (IOException ex) {
                logger.severe("Omg, this is so bad: " + e);
            }
            logStreamMap.remove(targetUuid);
        }
    }

    public void closeAll() {
        logger.info("Closing each player log stream...");
        for (UUID _uuid : logStreamMap.keySet()) {
            try {
                logStreamMap.get(_uuid).close();
            } catch (Exception e) {
                logger.warning("Failed to close log stream for " + _uuid + ": " + e);
            }
        }
        logger.info("Log stream was closed.");

        logger.info("Saving McIdManager data...");
        mcIdManager.save();
        logger.info("McIdManager data saved.");
    }

    private static String createLogLine(
            @NotNull String eventType,
            @NotNull String itemData,
            @NotNull String from,
            @NotNull String to,
            @Nullable String additionalMsg
    ) {
        StringJoiner sj = new StringJoiner(" ")
                .add(getTimestampStr())
                .add(eventType)
                .add(itemData)
                .add(from)
                .add("to")
                .add(to);
        if(additionalMsg != null) sj.add(additionalMsg);
        return sj.toString();
    }

    private static String getTimestampStr() {
        return LogDateFormats.TIMESTAMP.format(System.currentTimeMillis());
    }
}
