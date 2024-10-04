package org.esoteric_organisation.firework_wars_plugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.inventory.ItemStack;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.manager.CustomItemManager;

public class ResetInventoryCommand extends CommandAPICommand {
  public ResetInventoryCommand(FireworkWarsPlugin plugin) {
    super("reset");

    CustomItemManager customItemManager = plugin.getCustomItemManager();

    executesPlayer((player, args) -> {
      player.getInventory().clear();

      ItemStack item1 = customItemManager.getItem("firework_shotgun").getItem(player);
      ItemStack item2 = customItemManager.getItem("firework_shotgun_ammo").getItem(player, 64);

      ItemStack item3 = customItemManager.getItem("firework_rifle").getItem(player);
      ItemStack item4 = customItemManager.getItem("firework_rifle_ammo").getItem(player, 64);

      ItemStack item5 = customItemManager.getItem("player_compass").getItem(player);

      player.getInventory().addItem(item1, item2, item3, item4, item5);
    });

    register(plugin);
  }
}
