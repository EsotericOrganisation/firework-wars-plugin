package org.esoteric_organisation.firework_wars_plugin.items.misc;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.AbstractItem;
import org.esoteric_organisation.firework_wars_plugin.util.ItemBuilder;

public class FlintAndSteelItem extends AbstractItem {
    public FlintAndSteelItem(FireworkWarsPlugin plugin) {
        super(plugin, "flint_and_steel", Material.FLINT_AND_STEEL, 5, 5);
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder<>(plugin, itemMaterial)
                .setUnbreakable(true)
                .modifyMeta(meta -> pdcManager.setStringValue(meta, customItemIdKey, itemId))
                .build();
    }

    @Override
    public int getStackAmount() {
        return 1;
    }
}
