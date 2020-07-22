package me.ufo.poison.detached;

import com.google.gson.JsonObject;
import me.ufo.poison.shared.PoisonAction;
import me.ufo.poison.shared.PoisonChannel;
import me.ufo.poison.shared.Queue;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Poison {

  public static boolean DEBUG_MODE;

  private static Poison instance;
  private final Logger logger;
  private final Config config;
  private final JedisPool pool;

  private Poison() {
    this.logger = Logger.getLogger(Poison.class.getName());
    this.config = new Config();
    this.pool = new JedisPool(
        new GenericObjectPoolConfig(),
        this.config.getHost(),
        this.config.getPort()
    );

    for (String queue : config.getQueues()) {
      new Queue(queue);
    }

    new SubscriptionHandler(this, this.pool.getResource());
  }

  public void write(PoisonChannel channel, PoisonAction action, JsonObject data) {
    try (Jedis jedis = this.pool.getResource()) {
      final JsonObject out = new JsonObject();
      out.addProperty("action", action.name());
      out.add("data", data);
      jedis.publish(channel.toString(), out.toString());
    }
  }

  public void info(String info, String... args) {
    this.logger.log(Level.INFO, info, args);
  }

  public void error(String error) {
    this.logger.log(Level.SEVERE, error);
  }

  public static Poison getInstance() {
    return instance;
  }

  public static void main(String[] args) {
    instance = new Poison();
  }

}
