package org.esoteric.minecraft.plugins.fireworkwars.items.armor;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.TeamPlayer;
import org.esoteric.minecraft.plugins.fireworkwars.items.AbstractItem;
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.fireworkwars.util.ItemBuilder;

public class HeavyArmorItem extends AbstractItem<LeatherArmorMeta> {
    public HeavyArmorItem(FireworkWarsPlugin plugin) {
        super(plugin, "heavy_armor", Material.DIAMOND_CHESTPLATE, 3, 25);
    }

    @Override
    public ItemStack getItem(Player player) {
        TeamPlayer teamPlayer = TeamPlayer.from(player);
        Color color;

        if (teamPlayer == null) {
            color = null;
        } else {
            color = teamPlayer.getTeam().getTeamData().getColor();
        }

        return new ItemBuilder<LeatherArmorMeta>(plugin, itemMaterial)
            .setName(Message.HEAVY_ARMOR, player)
            .setLore(Message.HEAVY_ARMOR_LORE, player)
            .setUnbreakable(true)
            .modifyMeta(meta -> modifyArmorMeta(meta, color))
            .build();
    }

    private void modifyArmorMeta(LeatherArmorMeta meta, Color color) {
        super.modifyMeta(meta);
        meta.setColor(color);
        meta.addEnchant(Enchantment.BLAST_PROTECTION, 1, true);
    }

    @Override
    public int getStackAmount() {
        return 1;
    }
}
