package org.esoteric_organisation.firework_wars_plugin.items.guns;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.manager.AbstractItem;
import org.esoteric_organisation.firework_wars_plugin.util.ItemBuilder;
import org.esoteric_organisation.firework_wars_plugin.util.Keys;

public abstract class BaseAmmoItem extends AbstractItem {

  public BaseAmmoItem(FireworkWarsPlugin plugin, String ammoId, Material ammoMaterial) {
    super(plugin, ammoId, ammoMaterial);
  }

  protected ItemBuilder<ItemMeta> getBaseAmmoBuilder(Player player) {
    return new ItemBuilder<>(plugin, itemMaterial)
      .modifyMeta(meta -> {
        pdcManager.setStringValue(meta, customItemIdKey, itemId);
        pdcManager.setUUIDValue(meta, Keys.AMMO_OWNER_UUID, player.getUniqueId());
      });
  }
}
