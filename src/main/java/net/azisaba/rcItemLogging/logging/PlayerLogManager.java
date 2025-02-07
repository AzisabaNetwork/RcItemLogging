package net.azisaba.rcItemLogging.logging;

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

    public PlayerLogManager(Logger logger, File logFolderPath) {
        this.logger = logger;
        this.logFolderPath = new File(logFolderPath, "logs");
        this.logFolderPath.mkdirs();
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
            @NotNull UUID playerFrom,
            @NotNull UUID playerTo,
            @Nullable String additionalMsg
    ) {
        StringJoiner sj = new StringJoiner(" ")
                .add(getTimestampStr())
                .add(eventType)
                .add(itemData)
                .add(playerFrom.toString())
                .add("to")
                .add(playerTo.toString());
        if(additionalMsg != null) sj.add(additionalMsg);
        String _msg = sj.toString();
        _putSingle(playerFrom, _msg);
        _putSingle(playerTo, _msg);
    }

    private void _putSingle(UUID targetUuid, String line) {
        if(!logStreamMap.containsKey(targetUuid)) {
            openPlayerLog(targetUuid);
        }

        var writer = logStreamMap.get(targetUuid);
        try {
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
        for (UUID _uuid : logStreamMap.keySet()) {
            try {
                logStreamMap.get(_uuid).close();
            } catch (Exception e) {
                logger.warning("Failed to close log stream for " + _uuid + ": " + e);
            }
        }
    }

    private static String getTimestampStr() {
        return LogDateFormats.TIMESTAMP.format(System.currentTimeMillis());
    }
}
