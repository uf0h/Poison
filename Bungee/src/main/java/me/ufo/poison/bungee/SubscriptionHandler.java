package me.ufo.poison.bungee;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.ufo.poison.shared.PoisonAction;
import me.ufo.poison.shared.PoisonChannel;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

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
    this.subscriptionThread = new Thread(() -> jedis.subscribe(this.jps, PoisonChannel.BUNGEE.toString()));
    this.subscriptionThread.start();
  }

  private void handle(JsonObject object) {
    final PoisonAction action = PoisonAction.valueOf(object.get("action").getAsString());
    final JsonObject data = object.getAsJsonObject("data");
    if (PoisonPlugin.DEBUG_MODE) {
      this.plugin.info("Received `{0}` action from `{1}`.", action.name(), data.get("server").getAsString());
    }

    switch (action) {
      case PLAYER_SEND:
        final UUID uuid = FastUUID.parseUUID(data.get("uuid").getAsString());
        final String destination = data.get("destination").getAsString();
        final ServerInfo serverInfo = this.plugin.getProxy().getServerInfo(destination);
        final ProxiedPlayer player = this.plugin.getProxy().getPlayer(uuid);

        player.connect(serverInfo, (result, throwable) -> {
          if (!result) {

          }
        });
        break;
    }
  }

  @Override
  public void close() {
    this.jps.unsubscribe();
    this.subscriptionThread.interrupt();
  }

}
