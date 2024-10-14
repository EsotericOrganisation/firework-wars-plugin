package org.esoteric.minecraft.plugins.fireworkwars.items.guns;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.items.AbstractItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.ItemType;
import org.esoteric.minecraft.plugins.fireworkwars.util.ItemBuilder;
import org.esoteric.minecraft.plugins.fireworkwars.util.Keys;

public abstract class BaseAmmoItem<M extends ItemMeta> extends AbstractItem<M> {

    public BaseAmmoItem(FireworkWarsPlugin plugin, String ammoId, Material ammoMaterial, int weight, int value) {
        super(plugin, ammoId, ammoMaterial, weight, value, ItemType.AMMO);
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
