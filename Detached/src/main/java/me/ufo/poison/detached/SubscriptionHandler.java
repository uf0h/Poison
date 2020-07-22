package me.ufo.poison.detached;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.ufo.poison.shared.PoisonAction;
import me.ufo.poison.shared.PoisonChannel;
import me.ufo.poison.shared.Queue;
import me.ufo.poison.shared.QueuePlayer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public final class SubscriptionHandler implements AutoCloseable {

  private final Poison instance;
  private final JedisPubSub jps;
  private final Thread subscriptionThread;

  public SubscriptionHandler(Poison instance, Jedis jedis) {
    this.instance = instance;
    this.jps = new JedisPubSub() {
      @Override
      public void onMessage(String channel, String message) {
        SubscriptionHandler.this.handle(
            JsonParser.parseString(message).getAsJsonObject()
        );
      }
    };
    this.subscriptionThread = new Thread(() -> jedis.subscribe(this.jps, PoisonChannel.DETACHED.toString()));
    this.subscriptionThread.start();
  }

  private void handle(JsonObject object) {
    final PoisonAction action = PoisonAction.valueOf(object.get("action").getAsString());
    final JsonObject data = object.getAsJsonObject("data");

    if (Poison.DEBUG_MODE) {
      this.instance.info(
          "Received `{0}` action from `{1}`.",
          action.name(), data.get("server").getAsString()
      );
    }

    switch (action) {
      case PING:

        break;
      case REQUEST_HUB:
        this.instance.info("Received `{0}`.");
        break;
      case JOIN_QUEUE:
        final String uuid = data.get("uuid").getAsString();
        final String destination = data.get("destination").getAsString();
        this.instance.info("Received `{0}` to `{1}`.", action.name(), destination);

        final Queue queue = Queue.getByName(destination);
        queue.getQueue().add(new QueuePlayer(FastUUID.parseUUID(uuid), 100));


        break;
    }
  }

  @Override
  public void close() {
    this.jps.unsubscribe();
    this.subscriptionThread.interrupt();
  }

}
