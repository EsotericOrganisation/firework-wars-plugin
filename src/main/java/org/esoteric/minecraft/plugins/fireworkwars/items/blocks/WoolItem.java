package org.esoteric.minecraft.plugins.fireworkwars.items.blocks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.TeamPlayer;
import org.esoteric.minecraft.plugins.fireworkwars.items.AbstractItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.ItemType;
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.fireworkwars.util.ItemBuilder;
import org.esoteric.minecraft.plugins.fireworkwars.util.Util;

import java.util.List;

public class WoolItem extends AbstractItem<ItemMeta> {
    public WoolItem(FireworkWarsPlugin plugin) {
        super(plugin, "wool", Material.WHITE_WOOL, 105, 2, ItemType.BLOCK);
    }

    @Override
    public ItemStack getItem(Player player) {
        TeamPlayer teamPlayer = TeamPlayer.from(player);
        Material material;

        if (teamPlayer == null) {
            material = itemMaterial;
        } else {
            material = teamPlayer.getTeam().getWoolMaterial();
        }

        return new ItemBuilder<>(plugin, material)
            .setName(Message.WOOL, player)
            .setLore(Message.WOOL_LORE, player)
            .modifyMeta(this::modifyMeta)
            .build();
    }

    @Override
    public int getStackAmount() {
        return List.of(32, 48, 64).get(Util.randomInt(0, 2));
    }

    @Override
    public void updateItemTexts(ItemStack item, Player player) {
        item.setItemMeta(getItem(player).getItemMeta());
    }
}
