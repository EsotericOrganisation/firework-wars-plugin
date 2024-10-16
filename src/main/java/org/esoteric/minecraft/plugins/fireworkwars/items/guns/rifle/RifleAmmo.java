package org.esoteric.minecraft.plugins.fireworkwars.items.guns.rifle;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.items.guns.BaseAmmoItem;
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.fireworkwars.util.Util;

public class RifleAmmo extends BaseAmmoItem<ItemMeta> {

    public RifleAmmo(FireworkWarsPlugin plugin) {
        super(plugin, "firework_rifle_ammo", Material.GHAST_TEAR, 85, 2);
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
        return Util.randomInt(12, 16);
    }
}
