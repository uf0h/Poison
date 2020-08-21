package me.ufo.poison.lobby;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class PoisonPlugin extends JavaPlugin implements CommandExecutor {


    private final String server;

    public PoisonPlugin() {
        this.saveDefaultConfig();
        this.server = this.getConfig().getString("server.id");
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        this.getCommand("poison").setExecutor(this);

    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return false;
    }

}
