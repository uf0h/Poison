package me.ufo.poison.lobby;

import java.util.logging.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ufo.poison.shared.QPlayer;
import me.ufo.poison.shared.RedisAction;
import me.ufo.poison.shared.ServerInfo;
import me.ufo.poison.shared.ServerStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.redisnode.RedisNodes;
import org.redisson.client.RedisConnectionException;

public final class PoisonPlugin extends JavaPlugin implements CommandExecutor {

    private final Redis redis;
    private final Gson gson;
    private final String serverID;

    public PoisonPlugin() {
        this.saveDefaultConfig();
        this.redis = new Redis(
            this.getConfig().getString("redis.address"),
            this.getConfig().getInt("redis.port")
        );
        this.gson = new GsonBuilder().disableHtmlEscaping().create();
        this.serverID = this.getConfig().getString("server.id");
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
        this.getCommand("poison").setExecutor(this);

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            this.redis.publish(RedisAction.SERVER_UPDATE, this.getServerInfo());
        }, 50L, 50L);
    }

    @Override
    public void onDisable() {
        this.redis.close();
    }

    private String getServerInfo() {
        return this.gson.toJson(
            new ServerInfo(
                serverID,
                this.getServer().getOnlinePlayers().size(),
                this.getServer().hasWhitelist() ? ServerStatus.WHITELISTED : ServerStatus.ONLINE
            )
        );
    }

    public Redis getRedis() {
        return redis;
    }

    // debug
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player = (Player) sender;
        final QPlayer qPlayer = new QPlayer(player.getUniqueId(), "factions");

        this.redis.publish(RedisAction.PLAYER_SEND, this.gson.toJson(qPlayer));
        return false;
    }

}
