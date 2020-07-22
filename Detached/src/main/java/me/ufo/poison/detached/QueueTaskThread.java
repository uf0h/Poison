package me.ufo.poison.detached;

import me.ufo.poison.shared.Queue;
import me.ufo.poison.shared.QueuePlayer;
import me.ufo.poison.shared.ServerData;

public final class QueueTaskThread extends Thread {

  @Override
  public void run() {
    while(true) {
      for (Queue queue : Queue.getQueues()) {
        if (queue.getQueue().isEmpty()) {
          continue;
        }

        final ServerData server = ServerData.getByName(queue.getName());
        if (server.isWhitelisted()) {
          continue;
        }

        final QueuePlayer player = queue.getQueue().poll();


      }
      try {
        Thread.sleep(1000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
