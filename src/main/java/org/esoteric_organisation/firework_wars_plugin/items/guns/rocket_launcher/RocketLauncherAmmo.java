package org.esoteric_organisation.firework_wars_plugin.items.guns.rocket_launcher;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.manager.AbstractItem;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.esoteric_organisation.firework_wars_plugin.util.ItemBuilder;

public class RocketLauncherAmmo extends AbstractItem {

  public RocketLauncherAmmo(FireworkWarsPlugin plugin) {
    super(plugin, "rocket_launcher_ammo", Material.FIREWORK_ROCKET);
  }

  @Override
  public ItemStack getItem(Player player) {
    return new ItemBuilder<CrossbowMeta>(plugin, Material.CROSSBOW)
      .setName(plugin.getLanguageManager().getMessage(Message.ROCKET_LAUNCHER_AMMO, player))
      .setLore(plugin.getLanguageManager().getMessage(Message.ROCKET_LAUNCHER_AMMO_LORE, player))
      .build();
  }
}
