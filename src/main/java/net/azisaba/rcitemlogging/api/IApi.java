package net.azisaba.rcitemlogging.api;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IApi {
    /**
     * Add log to nameFrom & nameTo player's log files.
     * @param eventType EventType id
     * @param nameFrom event from
     * @param nameTo event to
     * @param message event detailed description
     * @param targets who this log append to
     */
    void put(
            @NotNull String eventType,
            @NotNull String nameFrom,
            @NotNull String nameTo,
            @NotNull String message,
            @NotNull UUID... targets
    );
}
