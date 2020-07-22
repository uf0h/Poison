package me.ufo.poison.hub;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class Locale {

  private final String QUEUE_JOIN;
  private final String QUEUE_POSITION;
  private final String SENDING;

  public Locale(FileConfiguration config) {
    final ConfigurationSection section = config.getConfigurationSection("messages");

    QUEUE_JOIN = Text.colorize(section.getString("joined", "&7You have joined the queue for &b%destination%&7."));
    QUEUE_POSITION = Text.colorize(
        section.getString("position", "&eYou are position &b%pos% &eout of &b%pos-max%&e.")
    );
    SENDING = Text.colorize(section.getString("sending", "&ePlease standby, you are being moved to the server..."));
  }

  public String joinMessage(String destination) {
    return StringUtils.replaceOnce(this.QUEUE_JOIN, "%destination%", destination);
  }

  public String positionMessage(int position, int max) {
    final String out = StringUtils.replaceOnce(this.QUEUE_POSITION, "%pos%", "" + position);
    return StringUtils.replaceOnce(out, "%max-pos%", "" + max);
  }

  public String sendingMessage() {
    return this.SENDING;
  }

}
