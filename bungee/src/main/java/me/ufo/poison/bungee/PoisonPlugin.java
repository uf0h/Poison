package me.ufo.poison.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;


public class PoisonPlugin extends Plugin {

    public PoisonPlugin() throws IOException {
        final PoisonBungeeConfig config = new PoisonBungeeConfig(this);
    }

    @Override
    public void onEnable() {
        this.getProxy().getPluginManager().registerCommand(this, new TestCommand());
    }

    @Override
    public void onDisable() {
    }

    public class TestCommand extends Command {
        public TestCommand() {
            super("poisoninfo");
        }

        @Override
        public void execute(CommandSender sender, String[] strings) {

        }
    }

}
