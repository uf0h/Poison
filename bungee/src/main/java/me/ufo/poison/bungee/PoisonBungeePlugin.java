package me.ufo.poison.bungee;

import java.io.IOException;
import java.util.logging.Level;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import org.redisson.api.redisnode.RedisNodes;
import org.redisson.client.RedisConnectionException;

public class PoisonBungeePlugin extends Plugin {

    private final Redis redis;

    public PoisonBungeePlugin() throws IOException {
        final PoisonBungeeConfig config = new PoisonBungeeConfig(this);
        this.redis = new Redis(
            config.getAddress(),
            config.getPort()
        );
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
        this.getProxy().getPluginManager().registerCommand(this, new TestCommand());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public class TestCommand extends Command {
        public TestCommand() {
            super("poison");
        }

        @Override
        public void execute(CommandSender sender, String[] strings) {

        }
    }

}
