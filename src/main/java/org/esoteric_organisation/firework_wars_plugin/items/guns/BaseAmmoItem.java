package org.esoteric_organisation.firework_wars_plugin.items.guns;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.AbstractItem;
import org.esoteric_organisation.firework_wars_plugin.util.ItemBuilder;
import org.esoteric_organisation.firework_wars_plugin.util.Keys;

public abstract class BaseAmmoItem extends AbstractItem {

    public BaseAmmoItem(FireworkWarsPlugin plugin, String ammoId, Material ammoMaterial, int weight, int value) {
        super(plugin, ammoId, ammoMaterial, weight, value);
    }

    protected ItemBuilder<ItemMeta> getBaseAmmoBuilder(Player player) {
        return new ItemBuilder<>(plugin, itemMaterial)
            .modifyMeta(meta -> {
                pdcManager.setStringValue(meta, customItemIdKey, itemId);

                if (player != null) {
                    pdcManager.setUUIDValue(meta, Keys.AMMO_OWNER_UUID, player.getUniqueId());
                } else {
                    pdcManager.setStringValue(meta, Keys.AMMO_OWNER_UUID, "");
                }
            });
    }
}
