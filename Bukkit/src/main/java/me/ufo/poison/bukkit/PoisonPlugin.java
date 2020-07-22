package me.ufo.poison.bukkit;

import com.eatthepath.uuid.FastUUID;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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

public final class PoisonPlugin extends JavaPlugin implements CommandExecutor {

  private static PoisonPlugin plugin;
  private final JedisPool pool;
  private final String serverID;

  public PoisonPlugin() {
    this.saveDefaultConfig();
    this.pool = new JedisPool(
        new GenericObjectPoolConfig(),
        this.getConfig().getString("redis.host"),
        this.getConfig().getInt("redis.port")
    );
    this.serverID = this.getConfig().getString("server-id");
  }

  @Override
  public void onEnable() {
    plugin = this;
    this.getServer().getMessenger().registerOutgoingPluginChannel(this, ":Poison");
    this.getCommand("hub").setExecutor(this);

    this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
      this.write(PoisonChannel.DETACHED, PoisonAction.PING, this.getServerInfo());
    }, 0L, 20L);
  }

  @Override
  public void onDisable() {
    super.onDisable();
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
    try (Jedis jedis = pool.getResource()) {
      final JsonObject out = new JsonObject();
      out.addProperty("action", action.name());
      out.add("data", data);
      jedis.publish(channel.name(), out.toString());
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) return false;

    final Player player = (Player) sender;

    this.sendToServer(player);
    return false;
  }

  private void sendToServer(Player player) {
    try {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("RequestHub");
      out.writeUTF(FastUUID.toString(player.getUniqueId()));

      player.sendPluginMessage(this, ":Poison", out.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static PoisonPlugin getPlugin() {
    return plugin;
  }

}
