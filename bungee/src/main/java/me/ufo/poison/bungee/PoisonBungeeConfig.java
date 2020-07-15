package me.ufo.poison.bungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public final class PoisonBungeeConfig {

    private final String address;
    private final int port;

    public PoisonBungeeConfig(PoisonBungeePlugin plugin) throws IOException {
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
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

}
