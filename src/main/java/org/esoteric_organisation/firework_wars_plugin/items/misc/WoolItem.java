package org.esoteric_organisation.firework_wars_plugin.items.misc;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.game.team.TeamPlayer;
import org.esoteric_organisation.firework_wars_plugin.items.AbstractItem;
import org.esoteric_organisation.firework_wars_plugin.util.ItemBuilder;
import org.esoteric_organisation.firework_wars_plugin.util.Util;

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
