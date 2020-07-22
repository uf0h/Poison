package me.ufo.poison.shared;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;

public final class Queue {

  private final static Set<Queue> QUEUES = new HashSet<>(2);

  // doubles as server name
  private final String name;
  private final PriorityQueue<QueuePlayer> pqueue;

  public Queue(String name) {
    this.name = name;
    this.pqueue = new PriorityQueue<>();

    QUEUES.add(this);
  }

  public String getName() {
    return name;
  }

  public PriorityQueue<QueuePlayer> getQueue() {
    return pqueue;
  }

  public boolean isPlayerQueued(UUID uuid) {
    for (QueuePlayer player : pqueue) {
      if (player.getUuid() == uuid) {
        return true;
      }
    }
    return false;
  }

  // TODO: better method needed
  public int getPosition(UUID uuid) {
    if (!this.isPlayerQueued(uuid)) {
      return 0;
    }

    final PriorityQueue<QueuePlayer> queue = new PriorityQueue<>(this.pqueue);

    int position = 0;
    while (!queue.isEmpty()) {
      final QueuePlayer player = queue.poll();
      if (player.getUuid().equals(uuid)) {
        break;
      }
      position++;
    }

    return position + 1;
  }

  public static Queue getByPlayer(UUID uuid) {
    for (Queue queue : QUEUES) {
      for (QueuePlayer player : queue.getQueue()) {
        if (player.getUuid().equals(uuid)) {
          return queue;
        }
      }
    }
    return null;
  }

  public static Queue getByName(String name) {
    for (Queue queue : QUEUES) {
      if (queue.getName().equalsIgnoreCase(name)) {
        return queue;
      }
    }
    return null;
  }

  public static Set<Queue> getQueues() {
    return QUEUES;
  }

}
