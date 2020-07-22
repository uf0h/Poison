package me.ufo.poison.shared;

import java.util.HashSet;
import java.util.Set;

public class ServerData {

  private static Set<ServerData> SERVERS = new HashSet<>();

  private String name;
  private boolean hub;
  private int onlinePlayers;
  private int maximumPlayers;
  private boolean whitelisted;
  private long lastUpdate;

  public ServerData(String name) {
    this.name = name;

    SERVERS.add(this);
  }

  public String getName() {
    return name;
  }

  public boolean isHub() {
    return hub;
  }

  public void setHub(boolean hub) {
    this.hub = hub;
  }

  public int getOnlinePlayers() {
    return onlinePlayers;
  }

  public void setOnlinePlayers(int onlinePlayers) {
    this.onlinePlayers = onlinePlayers;
  }

  public int getMaximumPlayers() {
    return maximumPlayers;
  }

  public void setMaximumPlayers(int maximumPlayers) {
    this.maximumPlayers = maximumPlayers;
  }

  public boolean isWhitelisted() {
    return whitelisted;
  }

  public void setWhitelisted(boolean whitelisted) {
    this.whitelisted = whitelisted;
  }

  public long getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(long lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public boolean isOnline() {
    return System.currentTimeMillis() - this.lastUpdate < 15000L;
  }

  public static ServerData getByName(String name) {
    for (ServerData server : SERVERS) {
      if (server.getName().equalsIgnoreCase(name)) {
        return server;
      }
    }

    return null;
  }

  public static Set<ServerData> getServers() {
    return SERVERS;
  }

}
