package me.ufo.poison.bukkit;

import java.util.logging.Level;
import me.ufo.poison.common.RedisAction;
import me.ufo.poison.common.ServerInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RTopic;
import org.redisson.api.redisnode.RedisNodes;
import org.redisson.client.RedisConnectionException;

public final class PoisonHubPlugin extends JavaPlugin implements CommandExecutor, Listener {

    private final Redis redis;
    private final ServerInfo serverInfo;

    public PoisonHubPlugin() {
        this.saveDefaultConfig();
        this.redis = new Redis(
            this.getConfig().getString("redis.address"),
            this.getConfig().getInt("redis.port")
        );
        this.serverInfo = new ServerInfo(this.getConfig().getString("server.id"));
    }

    @Override
    public void onLoad() {
        if (this.redis.getRedisson().getRedisNodes(RedisNodes.SINGLE).getInstance().ping()) {
            this.getLogger().log(Level.INFO, "Successfully connected to redis.");
        } else {
            throw new RedisConnectionException("Failed to connect to redis.");
        }
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("poison").setExecutor(this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        RTopic topic = this.getRedis().getRedisson().getTopic(RedisAction.SERVER_UPDATE.toString());
        sender.sendMessage(new String[]{
            "players: " + serverInfo.getPlayers(),
            "listeners: " + topic.countListeners(),
            "name: " + topic.getChannelNames()
        });

        this.serverInfo.setPlayers(this.serverInfo.getPlayers() + 1);
        this.redis.action(RedisAction.SERVER_UPDATE, this.serverInfo);

        return false;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        this.serverInfo.setPlayers(this.serverInfo.getPlayers() + 1);
        this.redis.action(RedisAction.SERVER_UPDATE, this.serverInfo);
    }

    public Redis getRedis() {
        return redis;
    }

}
