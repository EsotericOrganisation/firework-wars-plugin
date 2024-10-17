package org.esoteric.minecraft.plugins.games.firework.wars.items.guns.rpg;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.esoteric.minecraft.plugins.games.firework.wars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.games.firework.wars.items.guns.BaseAmmoItem;
import org.esoteric.minecraft.plugins.games.firework.wars.language.Message;
import org.esoteric.minecraft.plugins.games.firework.wars.util.Util;

public class RocketLauncherAmmo extends BaseAmmoItem<FireworkMeta> {

    public RocketLauncherAmmo(FireworkWarsPlugin plugin) {
        super(plugin, "rocket_launcher_ammo", Material.FIREWORK_ROCKET, 35, 4);
    }

    @Override
    public ItemStack getItem(Player player) {
        return getBaseAmmoBuilder(player)
            .setName(Message.ROCKET_LAUNCHER_AMMO, player)
            .setLore(Message.ROCKET_LAUNCHER_AMMO_LORE, player)
            .build();
    }

    @Override
    public int getStackAmount() {
        return Util.randomInt(1, 2);
    }
}
