package me.ufo.poison.common;

import java.io.Serializable;
import java.util.UUID;

public final class QPlayer implements Comparable<QPlayer>, Serializable {

    private static final long serialVersionUID = 2378023970063100258L;

    private final UUID uuid;
    private String currentServer;
    private int priority = 0;

    public QPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public QPlayer(UUID uuid, int priority) {
        this.uuid = uuid;
        this.priority = priority;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(QPlayer other) {
        if (this.priority < other.getPriority()) {
            return -1;
        } else if (this.priority == other.getPriority()) {
            // check who was first

            return 0;
        } else {
            return 1;
        }
    }

}
