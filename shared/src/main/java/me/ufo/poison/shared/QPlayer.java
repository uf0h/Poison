package me.ufo.poison.shared;

import java.io.Serializable;
import java.util.UUID;

public final class QPlayer implements Comparable<QPlayer>, Serializable {

    private static final long serialVersionUID = 2378023970063100258L;

    private final UUID uuid;
    private final String destination;
    private int priority = 100;

    public QPlayer(UUID uuid, String destination) {
        this.uuid = uuid;
        this.destination = destination;
    }

    public QPlayer(String uuid, String destination) {
        this.uuid = UUID.fromString(uuid);
        this.destination = destination;
    }

    public QPlayer(UUID uuid, int priority, String destination) {
        this.uuid = uuid;
        this.priority = priority;
        this.destination = destination;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getDestination() {
        return this.destination;
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
