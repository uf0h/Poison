package me.ufo.poison.bungee;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.ufo.poison.shared.QPlayer;
import me.ufo.poison.shared.Queue;
import me.ufo.poison.shared.RedisAction;
import me.ufo.poison.shared.ServerData;
import me.ufo.poison.shared.ServerStatus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import org.redisson.api.RPriorityQueue;


public class PoisonPlugin extends Plugin {

    private final Redis redis;
    private final JsonParser parser;

    public PoisonPlugin() throws IOException {
        final PoisonBungeeConfig config = new PoisonBungeeConfig(this);
        this.redis = new Redis(
            config.getAddress(),
            config.getPort()
        );
        this.parser = new JsonParser();
    }

    @Override
    public void onEnable() {
        this.redis.getRedisson().getTopic("POISON:" + RedisAction.SERVER_UPDATE.toString())
            .addListener(String.class, (channel, message) -> {
                final JsonObject object = this.parser.parse(message).getAsJsonObject();
                final String id = object.get("serverID").getAsString();
                final ServerData serverData = ServerData.getByName(id);

                serverData.setOnlinePlayers(object.get("onlinePlayers").getAsInt());
                serverData.setServerStatus(ServerStatus.valueOf(object.get("serverStatus").getAsString()));
                serverData.setLastPinged(System.currentTimeMillis());

                this.getLogger().log(Level.INFO, "Server " + id + " pinged.");
            });

        this.redis.getRedisson().getTopic("POISON:" + RedisAction.PLAYER_QUEUE_ADD.toString())
            .addListener(String.class, (channel, message) -> {
                final JsonObject object = this.parser.parse(message).getAsJsonObject();
                final String uuid = object.get("uuid").getAsString();
                final String destination = object.get("destination").getAsString();
                if (!Queue.getByName(destination).isEnabled()) {
                    this.getProxy().getPlayer(uuid)
                        .sendMessage(new TextComponent(ChatColor.RED.toString() + "This queue is not enabled."));
                }

                final QPlayer qPlayer = new QPlayer(object.get("uuid").getAsString(), destination);

                this.redis.getRedisson().getPriorityQueue(destination).add(qPlayer);
            });

        this.redis.getRedisson().getTopic("POISON:" + RedisAction.PLAYER_SEND.toString())
            .addListener(String.class, (channel, message) -> {
                final JsonObject object = this.parser.parse(message).getAsJsonObject();
                final UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                final ServerInfo to = this.getProxy().getServerInfo(object.get("destination").getAsString());

                this.getProxy().getPlayer(uuid).connect(to);
            });

        this.getProxy().getPluginManager().registerCommand(this, new TestCommand());

        new QueueThread().start();
    }

    @Override
    public void onDisable() {
        this.redis.close();
    }

    public class QueueThread extends Thread {

        @Override
        public void run() {
            while (true) {
                for (Queue q : Queue.getQueues()) {
                    final ServerData serverData = q.getServerData();

                    if (serverData.getServerStatus() == ServerStatus.WHITELISTED) {
                        continue;
                    }

                    if (!q.isEnabled()) {
                        continue;
                    }

                    final RPriorityQueue<QPlayer> priorityQueue = redis.getRedisson().getPriorityQueue(q.getName());
                    if (priorityQueue.isEmpty()) {
                        continue;
                    }

                    final QPlayer player = priorityQueue.poll();
                    if (player != null) {
                        try {
                            getProxy().getPlayer(player.getUUID()).connect(getProxy().getServerInfo(q.getName()));
                            getLogger().log(Level.INFO, "Sending " + player.getUUID() + " to " + q.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public class TestCommand extends Command {
        public TestCommand() {
            super("poisoninfo");
        }

        @Override
        public void execute(CommandSender sender, String[] strings) {
            for (ServerData data : ServerData.getServers()) {
                final StringBuilder info = new StringBuilder();

                info.append(ChatColor.YELLOW.toString())
                    .append("Server `").append(data.getServerID()).append("` info:")
                    .append("\n")
                    .append("  Online Players: ").append(data.getOnlinePlayers())
                    .append("\n")
                    .append("  Server Status: ").append(data.getServerStatus());

                final Queue queue = Queue.getByName(data.getServerID());
                if (queue != null) {
                    info.append("\n")
                        .append("  Queue Status: ").append(queue.isEnabled())
                        .append("\n")
                        .append("  Queue Players: ").append(redis.getRedisson().getPriorityQueue(queue.getName()).size());
                }

                sender.sendMessage(new TextComponent(info.toString()));
            }

        }
    }

}
