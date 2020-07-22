package me.ufo.poison.bungee;

import com.eatthepath.uuid.FastUUID;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.ufo.poison.shared.ServerData;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.UUID;

public final class PoisonListener implements Listener {

  private final PoisonPlugin plugin;

  public PoisonListener(PoisonPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPluginMessageEvent(PluginMessageEvent e) {
    if (!e.getTag().equalsIgnoreCase(":Poison")) return;
    final byte[] data = Arrays.copyOf(e.getData(), e.getData().length);

    this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
      final ByteArrayDataInput input = ByteStreams.newDataInput(data);
      final ByteArrayDataOutput output = ByteStreams.newDataOutput();
      final String channel = input.readUTF();

      String type;
      switch (channel) {
        case "RequestHub":
          final UUID uuid = FastUUID.parseUUID(input.readUTF());
          final ProxiedPlayer player = this.plugin.getProxy().getPlayer(uuid);

          player.connect(this.getLeastPopulatedHub(), (result, throwable) -> {
            if (!result) {

            }
          });
          break;
        default:
          break;
      }
    });
  }

  public ServerInfo getLeastPopulatedHub() {
    ServerData out = null;
    for (ServerData server : ServerData.getServers()) {
      if (server.isHub()) {
        if (out == null) {
          out = server;
          continue;
        }
        out = this.compare(out, server);
      }
    }

    return this.plugin.getProxy().getServerInfo(out.getName());
  }

  public ServerData compare(ServerData first, ServerData second) {
    int one = first.getOnlinePlayers();
    int two = second.getOnlinePlayers();
    if (one <= two) {
      return first;
    }
    return second;
  }

}
