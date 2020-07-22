package me.ufo.poison.shared;

import java.util.UUID;

public final class QueuePlayer implements Comparable<QueuePlayer> {

  private final UUID uuid;
  private final int priority;

  public QueuePlayer(UUID uuid, int priority) {
    this.uuid = uuid;
    this.priority = priority;
  }

  public UUID getUuid() {
    return uuid;
  }

  public int getPriority() {
    return priority;
  }

  @Override
  public int compareTo(QueuePlayer other) {
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
