package org.esoteric.minecraft.plugins.fireworkwars.items.misc;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.items.AbstractItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.ItemType;
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.fireworkwars.util.ItemBuilder;

public class FlintAndSteelItem extends AbstractItem<ItemMeta> {
    public FlintAndSteelItem(FireworkWarsPlugin plugin) {
        super(plugin, "flint_and_steel", Material.FLINT_AND_STEEL, 30, 5, ItemType.MISC);
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder<>(plugin, itemMaterial)
            .setName(Message.FLINT_AND_STEEL, player)
            .setLore(Message.FLINT_AND_STEEL_LORE, player)
            .setUnbreakable(true)
            .modifyMeta(this::modifyMeta)
            .build();
    }

    @Override
    public int getStackAmount() {
        return 1;
    }
}
