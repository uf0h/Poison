package me.ufo.poison.bungee;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Config {

  private String host;
  private int port;
  private String password;

  public Config(PoisonPlugin plugin) {
    if (!plugin.getDataFolder().exists()) {
      plugin.getDataFolder().mkdir();
    }

    File file = new File(plugin.getDataFolder(), "config.yml");
    if (!file.exists()) {
      try {
        try (InputStream in = plugin.getResourceAsStream(file.getName());
             FileOutputStream out = new FileOutputStream(file)) {

          final byte[] buffer = new byte[1024];
          int length;
          while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
          }

        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Configuration config = null;
    try {
      config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.host = config.getString("redis.host", "127.0.0.1");
    this.port = config.getInt("redis.port", 6379);
    this.password = config.getString("redis.password", "");

    file = null;
    config = null;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getPassword() {
    return password;
  }

}
