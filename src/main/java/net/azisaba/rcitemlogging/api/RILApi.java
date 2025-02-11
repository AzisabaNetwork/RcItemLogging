package net.azisaba.rcitemlogging.api;

import net.azisaba.rcitemlogging.RcItemLogging;
import org.jetbrains.annotations.NotNull;

public class RILApi implements IApi {
    private final RcItemLogging plugin;

    public RILApi(RcItemLogging plugin) {
        this.plugin = plugin;
    }

    @Override
    public void put(@NotNull String eventType, @NotNull String nameFrom, @NotNull String nameTo, @NotNull String message) {
        plugin.getLogManager().put(eventType, nameFrom, nameTo, message);
    }
}
