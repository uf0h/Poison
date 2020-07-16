package me.ufo.poison.bungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import com.google.common.io.ByteStreams;
import me.ufo.poison.shared.Queue;
import me.ufo.poison.shared.ServerData;
import me.ufo.poison.shared.ServerType;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public final class PoisonBungeeConfig {

    private final String address;
    private final int port;

    public PoisonBungeeConfig(PoisonPlugin plugin) throws IOException {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        final File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            file.createNewFile();
            try (InputStream in = plugin.getResourceAsStream("config.yml")) {
                final OutputStream out = new FileOutputStream(file);
                ByteStreams.copy(in, out);
            }
        }

        final Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

        this.address = config.getString("redis.address");
        this.port = config.getInt("redis.port");

        for (String lobby : config.getSection("queue-servers").getKeys()) {
            new ServerData(lobby, ServerType.LOBBY);
            plugin.getLogger().log(Level.INFO, "+ `" + lobby + "` lobby server.");
        }

        for (String destination : config.getSection("destination-servers").getKeys()) {
            new ServerData(destination, ServerType.DESTINATION);
            plugin.getLogger().log(Level.INFO, "+ `" + destination + "` destination server.");
            new Queue(destination);
            plugin.getLogger().log(Level.INFO, "+ `" + destination + "` queue added.");
        }
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

}
