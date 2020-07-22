package me.ufo.poison.bungee;

import com.google.gson.JsonObject;
import me.ufo.poison.shared.PoisonAction;
import me.ufo.poison.shared.PoisonChannel;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class PoisonPlugin extends Plugin {

  public static boolean DEBUG_MODE;

  private static PoisonPlugin instance;
  private final Config config;
  private final JedisPool pool;

  private SubscriptionHandler subscriptionHandler;

  public PoisonPlugin() {
    instance = this;
    this.config = new Config(this);
    this.pool = new JedisPool(
        new GenericObjectPoolConfig(),
        this.config.getHost(),
        this.config.getPort()
    );
  }

  @Override
  public void onEnable() {
    this.subscriptionHandler = new SubscriptionHandler(this, this.pool.getResource());
    this.getProxy().getPluginManager().registerListener(this, new PoisonListener(this));
  }

  @Override
  public void onDisable() {
    this.pool.close();
    this.subscriptionHandler.close();
  }

  public void write(PoisonChannel channel, PoisonAction action, JsonObject data) {
    try (Jedis jedis = pool.getResource()) {
      final JsonObject out = new JsonObject();
      out.addProperty("action", action.name());
      out.add("data", data);
      jedis.publish(channel.name(), out.toString());
    }
  }

  public void info(String info, String... args) {
    this.getLogger().log(Level.INFO, info, args);
  }

  public static PoisonPlugin getInstance() {
    return instance;
  }

}
