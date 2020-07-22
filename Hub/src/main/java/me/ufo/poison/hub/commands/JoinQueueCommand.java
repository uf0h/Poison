package me.ufo.poison.hub.commands;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.JsonObject;
import me.ufo.poison.hub.PoisonPlugin;
import me.ufo.poison.hub.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class JoinQueueCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) return false;

    if (args.length != 1) {
      sender.sendMessage(Text.colorize("&7Usage: &b/joinqueue <queue>"));
      return false;
    }

    final Player player = (Player) sender;

    if (PoisonPlugin.getInstance().isQueued(player.getUniqueId())) {
      sender.sendMessage(Text.colorize("&7You are already in a queue."));
      return false;
    }

    if (!PoisonPlugin.getInstance().isValidQueue(args[0])) {
      sender.sendMessage(Text.colorize("&7That is not a valid queue."));
      return false;
    }



    // TODO: get priorities
    int priority = 100;

    JsonObject out = new JsonObject();
    out.addProperty("uuid", FastUUID.toString(player.getUniqueId()));
    out.addProperty("priority", priority);
    out.addProperty("destination", args[0]);

    PoisonPlugin.getInstance().write(PoisonChannel.DETACHED, PoisonAction.JOIN_QUEUE, out);
    return false;
  }

}
