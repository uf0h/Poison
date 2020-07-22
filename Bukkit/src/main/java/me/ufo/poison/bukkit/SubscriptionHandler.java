package me.ufo.poison.bukkit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.ufo.poison.shared.PoisonAction;
import me.ufo.poison.shared.PoisonChannel;
import me.ufo.poison.shared.ServerData;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public final class SubscriptionHandler implements AutoCloseable {

  private final PoisonPlugin plugin;
  private final JsonParser parser;
  private final JedisPubSub jps;
  private final Thread subscriptionThread;

  public SubscriptionHandler(PoisonPlugin plugin, Jedis jedis) {
    this.plugin = plugin;
    this.parser = new JsonParser();
    this.jps = new JedisPubSub() {
      @Override
      public void onMessage(String channel, String message) {
        SubscriptionHandler.this.handle(
            SubscriptionHandler.this.parser.parse(message).getAsJsonObject()
        );
      }
    };
    this.subscriptionThread = new Thread(() -> jedis.subscribe(this.jps, PoisonChannel.HUB.toString()));
    this.subscriptionThread.start();
  }

  private void handle(JsonObject object) {
    final PoisonAction action = PoisonAction.valueOf(object.get("action").getAsString());
    final JsonObject data = object.getAsJsonObject("data");

    switch (action) {
      case SEND_ALL_SERVER_DATA:
        for (JsonElement element : object.get("servers").getAsJsonArray()) {
          final JsonObject server = element.getAsJsonObject();
          final String name = server.get("name").getAsString();
          ServerData serverData = ServerData.getByName(name);

          if (serverData == null) {
            serverData = new ServerData(name);
          }

          serverData.setOnlinePlayers(server.get("online-players").getAsInt());
          serverData.setMaximumPlayers(server.get("maximum-players").getAsInt());
          serverData.setWhitelisted(server.get("whitelisted").getAsBoolean());
          serverData.setLastUpdate(server.get("last-update").getAsLong());
        }
        break;
    }
  }

  @Override
  public void close() {
    this.jps.unsubscribe();
    this.subscriptionThread.interrupt();
  }

}
