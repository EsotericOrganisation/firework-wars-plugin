package org.esoteric_organisation.firework_wars_plugin.items.guns.rocket_launcher;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.guns.BaseAmmoItem;
import org.esoteric_organisation.firework_wars_plugin.language.Message;

public class RocketLauncherAmmo extends BaseAmmoItem {

    public RocketLauncherAmmo(FireworkWarsPlugin plugin) {
        super(plugin, "rocket_launcher_ammo", Material.FIREWORK_ROCKET);
    }

    @Override
    public ItemStack getItem(Player player) {
        return getBaseAmmoBuilder(player)
            .setName(Message.ROCKET_LAUNCHER_AMMO, player)
            .setLore(Message.ROCKET_LAUNCHER_AMMO_LORE, player)
            .build();
    }
}
