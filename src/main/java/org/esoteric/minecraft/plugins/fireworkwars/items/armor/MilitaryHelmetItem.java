package org.esoteric.minecraft.plugins.fireworkwars.items.armor;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.items.AbstractItem;
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.fireworkwars.util.ItemBuilder;

public class MilitaryHelmetItem extends AbstractItem<ArmorMeta> {
    public MilitaryHelmetItem(FireworkWarsPlugin plugin) {
        super(plugin, "military_helmet", Material.TURTLE_HELMET, 4, 15);
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder<ArmorMeta>(plugin, itemMaterial)
            .setName(Message.MILITARY_HELMET, player)
            .setLore(Message.MILITARY_HELMET_LORE, player)
            .setUnbreakable(true)
            .modifyMeta(this::modifyMeta)
            .build();
    }

    @Override
    protected void modifyMeta(ArmorMeta meta) {
        super.modifyMeta(meta);
        meta.addEnchant(Enchantment.BLAST_PROTECTION, 1, true);
    }

    @Override
    public int getStackAmount() {
        return 1;
    }
}
