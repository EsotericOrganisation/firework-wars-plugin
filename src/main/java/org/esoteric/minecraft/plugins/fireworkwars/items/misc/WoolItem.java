package org.esoteric.minecraft.plugins.fireworkwars.items.misc;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.TeamPlayer;
import org.esoteric.minecraft.plugins.fireworkwars.items.AbstractItem;
import org.esoteric.minecraft.plugins.fireworkwars.util.ItemBuilder;
import org.esoteric.minecraft.plugins.fireworkwars.util.Util;

public class WoolItem extends AbstractItem {
    public WoolItem(FireworkWarsPlugin plugin) {
        super(plugin, "wool", Material.WHITE_WOOL, 15, 1);
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
                .modifyMeta(meta -> pdcManager.setStringValue(meta, customItemIdKey, itemId))
                .build();
    }

    @Override
    public int getStackAmount() {
        return Util.randomInt(16, 32);
    }
}
