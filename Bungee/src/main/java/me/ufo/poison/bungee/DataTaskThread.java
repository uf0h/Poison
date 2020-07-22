package me.ufo.poison.bungee;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.ufo.poison.shared.PoisonAction;
import me.ufo.poison.shared.PoisonChannel;
import me.ufo.poison.shared.ServerData;

public final class DataTaskThread extends Thread {

  @Override
  public void run() {
    while (true) {
      JsonArray servers = new JsonArray();
      for (ServerData serverData : ServerData.getServers()) {
        JsonObject data = new JsonObject();
        data.addProperty("name", serverData.getName());
        data.addProperty("online-players", serverData.getOnlinePlayers());
        data.addProperty("maximum-players", serverData.getMaximumPlayers());
        data.addProperty("whitelisted", serverData.isWhitelisted());
        data.addProperty("last-update", serverData.getLastUpdate());

        servers.add(data);
      }

      JsonObject out = new JsonObject();
      out.add("servers", servers);

      PoisonPlugin.getInstance().write(PoisonChannel.BUKKIT, PoisonAction.SEND_ALL_SERVER_DATA, out);

      servers = null;
      out = null;
      try {
        Thread.sleep(3000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
