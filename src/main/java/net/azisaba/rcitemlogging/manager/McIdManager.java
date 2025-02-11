package net.azisaba.rcitemlogging.manager;

import net.azisaba.rcitemlogging.util.ConditionChainer;
import net.azisaba.rcitemlogging.util.LogDateFormats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Watch mcid changes for logging
 */
public class McIdManager implements Listener {
    private static File getOldFileName(File targetFile) {
        return new File(targetFile.getParent(), targetFile.getName() + "_old_" + LogDateFormats.getTimestampStr());
    }

    private final ConcurrentHashMap<UUID, String> mcIdMap = new ConcurrentHashMap<>();
    private final LogManager logManager;
    private final File mcIdDataFile;
    private final Logger logger;

    public McIdManager(Logger logger, LogManager logManager, File mcIdDataFile) {
        this.logger = logger;
        this.logManager = logManager;
        this.mcIdDataFile = mcIdDataFile;
        new ConditionChainer()
                .then(this.mcIdDataFile::exists)
                .then(() -> !load())
                .then(() -> !this.mcIdDataFile.renameTo(getOldFileName(this.mcIdDataFile)))
                .atLast(() -> logger.severe("Failed to rename old file. Please check file permission."))
                .run();
    }


    public boolean load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(mcIdDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                var split_line = line.split(":");
                if (split_line.length != 2) break;
                mcIdMap.put(UUID.fromString(split_line[0]), split_line[1]);
            }
        } catch (IOException e) {
            logger.warning("Failed to load mcid data: " + e);
            return false;
        }
        return true;
    }

    public void save() {
        try(FileWriter writer = new FileWriter(mcIdDataFile)) {
            for(UUID key: mcIdMap.keySet()) {
                writer.write(key + ":" + mcIdMap.get(key) + System.lineSeparator());
            }
            writer.flush();
        } catch (Exception e) {
            logger.warning("Failed to save mcid: " + e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Fresh data
        Player player = event.getPlayer();
        String mcid = player.getName();
        UUID uuid = player.getUniqueId();

        // Stored data
        String storedMcid = mcIdMap.getOrDefault(uuid, "#none");

        // Compare fresh & stored data
        if(!storedMcid.equals(mcid)) {
            mcIdMap.put(uuid, mcid);
            logManager.put("update_mcid", storedMcid, mcid, "mcid change detected.", uuid);
        }
    }

    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent event) {
        // Close user's log stream
        logManager.close(event.getPlayer().getUniqueId());
    }
}
