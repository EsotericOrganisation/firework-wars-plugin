package org.esoteric_organisation.firework_wars_plugin.items.guns.shotgun;

import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.manager.AbstractItem;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.esoteric_organisation.firework_wars_plugin.util.ItemBuilder;
import org.esoteric_organisation.firework_wars_plugin.util.Keys;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShotgunAmmo extends AbstractItem {
  public ShotgunAmmo(FireworkWarsPlugin plugin) {
    super(plugin, "firework_shotgun_ammo", Material.NETHER_WART);
  }

  @Override
  public ItemStack getItem(Player player) {
    return new ItemBuilder<>(plugin, itemMaterial).setName(languageManager.getMessage(Message.FIREWORK_SHOTGUN_AMMO, player))
        .setLore(languageManager.getMessage(Message.FIREWORK_SHOTGUN_AMMO_LORE, player)).modifyMeta(meta -> {
          pdcManager.setStringValue(meta, isItemKey, itemId);
          pdcManager.setStringValue(meta, Keys.AMMO_OWNER_UUID, player.getUniqueId().toString());
        }).build();
  }
}
