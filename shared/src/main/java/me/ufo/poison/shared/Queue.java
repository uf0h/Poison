package me.ufo.poison.shared;

import java.util.HashSet;
import java.util.Set;

public final class Queue {

    private static final Set<Queue> QUEUES = new HashSet<>(8);

    private final String name;
    private boolean enabled;

    public Queue(String name) {
        this.name = name;

        QUEUES.add(this);
    }

    public static Set<Queue> getQueues() {
        return QUEUES;
    }

    public static Queue getByName(String name) {
        for (Queue queue : QUEUES) {
            if (name.equalsIgnoreCase(queue.getName())) {
                return queue;
            }
        }

        return null;
    }

    public String getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ServerData getServerData() {
        return ServerData.getByName(this.name);
    }

}
