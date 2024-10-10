package org.esoteric_organisation.firework_wars_plugin.items.explosives;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.AbstractItem;
import org.esoteric_organisation.firework_wars_plugin.util.ItemBuilder;
import org.esoteric_organisation.firework_wars_plugin.util.Util;

public class TNTItem extends AbstractItem {
    public TNTItem(FireworkWarsPlugin plugin) {
        super(plugin, "tnt", Material.TNT, 5, 9);
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder<>(plugin, itemMaterial)
                .modifyMeta(meta -> pdcManager.setStringValue(meta, customItemIdKey, itemId))
                .build();
    }

    @Override
    public int getStackAmount() {
        return Util.randomInt(3, 5);
    }
}
