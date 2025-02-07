package net.azisaba.rcItemLogging.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
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

    public void put(UUID targetUuid) {
        if(!logStreamMap.containsKey(targetUuid)) {
            openPlayerLog(targetUuid);
        }

        var writer = logStreamMap.get(targetUuid);
        try {
            writer.write("Test Message!!");
            writer.flush();
        } catch (IOException e) {
            logger.severe("Failed to write log data: " + e);
        }
    }

    public void closeAll() {
        for(UUID _uuid: logStreamMap.keySet()) {
            try {
                logStreamMap.get(_uuid).close();
            } catch (Exception e) {
                logger.warning("Failed to close log stream for " + _uuid + ": " + e);
            }
        }
    }
}
