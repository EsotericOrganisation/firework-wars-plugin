package org.esoteric_organisation.firework_wars_plugin.items.guns.shotgun;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.guns.BaseAmmoItem;
import org.esoteric_organisation.firework_wars_plugin.language.Message;

public class ShotgunAmmo extends BaseAmmoItem {
  public ShotgunAmmo(FireworkWarsPlugin plugin) {
    super(plugin, "firework_shotgun_ammo", Material.NETHER_WART);
  }

  @Override
  public ItemStack getItem(Player player) {
    return getBaseAmmoBuilder(player)
      .setName(Message.FIREWORK_SHOTGUN_AMMO, player)
      .setLore(Message.FIREWORK_SHOTGUN_AMMO_LORE, player)
      .build();
  }
}
