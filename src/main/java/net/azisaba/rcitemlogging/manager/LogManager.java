package net.azisaba.rcitemlogging.manager;

import net.azisaba.rcitemlogging.util.LogDateFormats;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Manage player log streams
 */
public class LogManager {
    private final ConcurrentHashMap<UUID, FileWriter> logStreamMap = new ConcurrentHashMap<>();
    private final Logger logger;
    private final File logFolder;

    public LogManager(@NotNull Logger logger, @NotNull File dataFolder) {
        this.logger = logger;
        this.logFolder = new File(dataFolder, "logs");
        if(!this.logFolder.exists()) {
            this.logFolder.mkdirs(); // TODO: handle this
        }
    }

    public void put(
            @NotNull String eventType,
            @NotNull String from,
            @NotNull String to,
            @NotNull String message,
            @NotNull UUID ...targets
    ) {
        // Create log line
        String _line = new StringJoiner(", ")
                .add(LogDateFormats.getTimestampStr())
                .add(eventType)
                .add(from + " to " + to)
                .add(message)
                .toString();
        _line += System.lineSeparator();

        // Put log line to each user log file
        for(UUID target: targets) {
            // If not opened yet, try to open log stream.
            if(!logStreamMap.containsKey(target) && !openPlayerLog(target)) {
                logger.warning("Failed to open log file stream for " + target);
                continue;
            }

            // Get & Write to log stream.
            try {
                var logStream = logStreamMap.get(target);
                logStream.write(_line);
                logStream.flush();
            } catch (IOException e) {
                logger.severe("Failed to write log line: " + e);
            }
        }
    }

    private boolean openPlayerLog(@NotNull UUID targetUuid) {
        File targetFile = new File(logFolder, targetUuid + ".log");
        try {
            FileWriter writer = new FileWriter(targetFile, true);
            logStreamMap.put(targetUuid, writer);
            return true;
        } catch (Exception e) {
            logger.severe("Failed to open player log writer: " + e);
            return false;
        }
    }

    public void close(@NotNull UUID playerUuid) {
        if(logStreamMap.containsKey(playerUuid)) {
            try {
                logStreamMap.remove(playerUuid).close();
                logger.finest("Log stream closed. (UUID:" + playerUuid + ")");
            } catch (IOException e) {
                logger.warning("Failed to close log stream gracefully: " + e);
            }
        } else {
            logger.warning("This player's log stream wasn't opened.");
        }
    }

    public void closeAll() {
        for(UUID uuid: logStreamMap.keySet()) {
            close(uuid);
        }
    }
}
