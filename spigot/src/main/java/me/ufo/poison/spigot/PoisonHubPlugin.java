package me.ufo.poison.spigot;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ufo.poison.common.RedisAction;
import me.ufo.poison.common.ServerInfo;
import me.ufo.poison.common.ServerStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.redisnode.RedisNodes;
import org.redisson.client.RedisConnectionException;

public final class PoisonHubPlugin extends JavaPlugin implements CommandExecutor {

    private final Redis redis;
    private final Gson gson;
    private final String serverID;

    public PoisonHubPlugin() {
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

    }

    // debug
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final ServerInfo info =
            new ServerInfo(this.serverID, ThreadLocalRandom.current().nextInt(0, 100), ServerStatus.ONLINE);
        this.redis.publish(RedisAction.SERVER_UPDATE, this.gson.toJson(info));
        return false;
    }

    public Redis getRedis() {
        return redis;
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

}
