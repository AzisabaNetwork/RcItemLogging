package net.azisaba.rcItemLogging.manager;

import net.azisaba.rcItemLogging.logging.LogDateFormats;
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

    public PlayerLogManager(Logger logger, File logFolderPath) {
        this.logger = logger;
        this.logFolderPath = new File(logFolderPath, "logs");
        this.logFolderPath.mkdirs();
        this.mcIdManager = new McIdManager(logger, new File(logFolderPath, "mcids.txt"));
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
        StringJoiner sj = new StringJoiner(" ")
                .add(getTimestampStr())
                .add(eventType)
                .add(itemData)
                .add(playerFrom.getName())
                .add("to")
                .add(playerTo.getName());
        if(additionalMsg != null) sj.add(additionalMsg);
        String _msg = sj.toString();
        _putSingle(playerFrom, _msg);
        _putSingle(playerTo, _msg);
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

    private static String getTimestampStr() {
        return LogDateFormats.TIMESTAMP.format(System.currentTimeMillis());
    }
}
