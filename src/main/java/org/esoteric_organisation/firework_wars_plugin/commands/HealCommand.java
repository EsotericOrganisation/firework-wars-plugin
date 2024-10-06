package org.esoteric_organisation.firework_wars_plugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class HealCommand extends CommandAPICommand {
  public HealCommand() {
    super("heal");

    executesPlayer((player, args) -> {
      player.setHealth(20.0D);
      player.setFoodLevel(20);
      player.setSaturation(20.0F);

      player.sendMessage(Component.text("You have been healed!").color(NamedTextColor.GREEN));
    });

    register();
  }
}
