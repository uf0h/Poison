package me.ufo.poison.detached;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class Config {

  private String host;
  private int port;
  private String password;

  private String[] queues;

  public Config() {
    File config = new File("config.properties");
    Properties properties = new Properties();

    if (config.exists()) {
      try (FileInputStream input = new FileInputStream(config)) {
        properties.load(input);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      try (FileOutputStream output = new FileOutputStream(config)) {
        properties.setProperty("redis.host", "127.0.0.1");
        properties.setProperty("redis.port", "6379");
        properties.setProperty("redis.password", "");
        properties.setProperty("queues", "");

        properties.store(output, null);
        System.out.println(properties);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    this.host = (String) properties.getOrDefault("redis.host", "127.0.0.1");
    this.port = Integer.parseInt((String) properties.getOrDefault("redis.port", 6379));
    this.password = (String) properties.getOrDefault("redis.password", "");
    this.queues = properties.getOrDefault("queues", "").toString().split(",");

    properties = null;
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

  public String[] getQueues() {
    return queues;
  }

}
