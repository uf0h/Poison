package me.ufo.poison.bungee;

import java.io.IOException;
import java.util.logging.Level;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.ufo.poison.common.ServerStatus;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import org.redisson.api.RTopicReactive;
import org.redisson.api.listener.MessageListener;

import static me.ufo.poison.common.RedisAction.SERVER_UPDATE;

public class PoisonBungeePlugin extends Plugin {

    private final Redis redis;
    private final JsonParser parser;

    public PoisonBungeePlugin() throws IOException {
        final PoisonBungeeConfig config = new PoisonBungeeConfig(this);
        this.redis = new Redis(
            config.getAddress(),
            config.getPort()
        );
        this.parser = new JsonParser();
    }

    @Override
    public void onEnable() {
        this.getProxy().getPluginManager().registerCommand(this, new TestCommand());

        final RTopicReactive serverUpdate = redis.getRedisson().getTopic("POISON:" + SERVER_UPDATE.toString());
        serverUpdate.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String message) {
                handle(parser.parse(message).getAsJsonObject());
            }
        }).subscribe();
    }

    public void handle(JsonObject object) {
        this.getLogger().log(Level.INFO, "Receiving update from: " + object.get("serverID"));
        this.getLogger().log(Level.INFO, object.toString());

        final ServerStatus status = ServerStatus.valueOf(object.get("serverStatus").getAsString());
        switch (status) {
            case WHITELISTED:

        }
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
