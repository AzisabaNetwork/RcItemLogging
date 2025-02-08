package net.azisaba.rcitemlogging.manager;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

public final class McIdManager {
    private final HashMap<UUID, String> mcIdMap = new HashMap<>();
    private final Logger logger;
    private final File mcIdDataFile;

    public McIdManager(Logger logger, File mcIdDataFile) {
        this.logger = logger;
        this.mcIdDataFile = mcIdDataFile;
        if(mcIdDataFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(mcIdDataFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    var split_line = line.split(":");
                    if (split_line.length != 2) break;
                    mcIdMap.put(UUID.fromString(split_line[0]), split_line[1]);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nullable
    public String getMcIdChanges(UUID uuid, String mcid) {
        var nowMcId = mcIdMap.getOrDefault(uuid, "");
        if(!nowMcId.equals(mcid)) {
            mcIdMap.put(uuid, mcid);
            return "Updated mcid " + nowMcId + " to " + mcid;
        }
        return null;
    }

    @Nullable
    public String getMcIdChanges(OfflinePlayer player) {
        return getMcIdChanges(player.getUniqueId(), player.getName());
    }

    public void checkAll(Function<UUID, String> uuidToMcId) {
        for(UUID key: mcIdMap.keySet()) {
            String actualMcId = uuidToMcId.apply(key);
            if(!mcIdMap.get(key).equals(actualMcId)) {
                mcIdMap.put(key, actualMcId);
                logger.info("Updated mcid for " + key + " / mcid:" + actualMcId);
            }
        }
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
}
