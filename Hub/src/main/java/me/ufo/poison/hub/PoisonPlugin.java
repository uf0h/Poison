package me.ufo.poison.hub;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.JsonObject;
import me.ufo.poison.shared.PoisonAction;
import me.ufo.poison.shared.PoisonChannel;
import me.ufo.poison.shared.ServerStatus;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PoisonPlugin extends JavaPlugin implements CommandExecutor {

  public static boolean DEBUG_MODE;

  private static PoisonPlugin instance;
  private final JedisPool jedisPool;
  private final Locale locale;

  private final String serverID;
  private final String[] queues;
  private final Set<UUID> queued;

  public PoisonPlugin() {
    this.saveDefaultConfig();
    this.jedisPool = new JedisPool(
        new GenericObjectPoolConfig(),
        this.getConfig().getString("redis.host"),
        this.getConfig().getInt("redis.port")
    );
    this.locale = new Locale(this.getConfig());
    this.serverID = this.getConfig().getString("server-id");
    this.queues = this.getConfig().getString("queues").split(",");
    this.queued = new HashSet<>(200);
  }

  @Override
  public void onEnable() {
    instance = this;
    this.getCommand("joinqueue").setExecutor(this);

    this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
      this.write(PoisonChannel.DETACHED, PoisonAction.PING, this.getServerInfo());
    }, 0L, 20L);
  }

  @Override
  public void onDisable() {
    this.jedisPool.close();
  }

  private JsonObject getServerInfo() {
    final JsonObject out = new JsonObject();
    out.addProperty("server-id", this.serverID);
    out.addProperty("online-players", this.getServer().getOnlinePlayers().size());
    out.addProperty("server-status",
        this.getServer().hasWhitelist() ? ServerStatus.WHITELISTED.name() : ServerStatus.ONLINE.name());
    return out;
  }

  public void write(PoisonChannel channel, PoisonAction action, JsonObject data) {
    try (Jedis jedis = jedisPool.getResource()) {
      final JsonObject out = new JsonObject();
      out.addProperty("action", action.name());
      out.add("data", data);
      jedis.publish(channel.name(), out.toString());
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) return false;

    final JsonObject out = new JsonObject();
    out.addProperty("uuid", FastUUID.toString(((Player) sender).getUniqueId()));
    this.write(PoisonChannel.DETACHED, PoisonAction.REQUEST_HUB, out);

    out.addProperty("destination", "factions");
    this.write(PoisonChannel.BUNGEE, PoisonAction.JOIN_QUEUE, out);
    return false;
  }

  public void addToQueued(UUID uuid) {
    this.queued.add(uuid);
  }

  public void removeFromQueued(UUID uuid) {
    this.queued.remove(uuid);
  }

  public boolean isValidQueue(String input) {
    for (String queue : this.queues) {
      if (queue.equalsIgnoreCase(input)) {
        return true;
      }
    }
    return false;
  }

  public boolean isQueued(UUID uuid) {
    for (UUID oUUID : queued) {
      if (uuid.equals(oUUID)) {
        return true;
      }
    }
    return false;
  }

  public Locale getLocale() {
    return locale;
  }

  public static PoisonPlugin getInstance() {
    return instance;
  }

}
