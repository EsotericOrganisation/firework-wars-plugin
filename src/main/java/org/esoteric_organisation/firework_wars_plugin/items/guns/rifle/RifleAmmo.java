package org.esoteric_organisation.firework_wars_plugin.items.guns.rifle;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.guns.BaseAmmoItem;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.esoteric_organisation.firework_wars_plugin.util.Util;

public class RifleAmmo extends BaseAmmoItem {

    public RifleAmmo(FireworkWarsPlugin plugin) {
        super(plugin, "firework_rifle_ammo", Material.GHAST_TEAR, 8, 2);
    }

    @Override
    public ItemStack getItem(Player player) {
        return getBaseAmmoBuilder(player)
            .setName(Message.FIREWORK_RIFLE_AMMO, player)
            .setLore(Message.FIREWORK_RIFLE_AMMO_LORE, player)
            .build();
    }

    @Override
    public int getStackAmount() {
        return Util.randomInt(8, 12);
    }
}
