package org.esoteric.minecraft.plugins.games.firework.wars.items.guns.shotgun;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.esoteric.minecraft.plugins.games.firework.wars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.games.firework.wars.items.guns.BaseAmmoItem;
import org.esoteric.minecraft.plugins.games.firework.wars.language.Message;
import org.esoteric.minecraft.plugins.games.firework.wars.util.Util;

public class ShotgunAmmo extends BaseAmmoItem<ItemMeta> {
    public ShotgunAmmo(FireworkWarsPlugin plugin) {
        super(plugin, "firework_shotgun_ammo", Material.NETHER_WART, 45, 3);
    }

    @Override
    public ItemStack getItem(Player player) {
        return getBaseAmmoBuilder(player)
            .setName(Message.FIREWORK_SHOTGUN_AMMO, player)
            .setLore(Message.FIREWORK_SHOTGUN_AMMO_LORE, player)
            .build();
    }

    @Override
    public int getStackAmount() {
        return Util.randomInt(4, 8);
    }
}
