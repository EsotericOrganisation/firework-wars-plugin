package org.esoteric.minecraft.plugins.fireworkwars.commands;

import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.items.CustomItemManager;

public class ResetInventoryCommand extends CommandAPICommand {
    public ResetInventoryCommand(FireworkWarsPlugin plugin) {
        super("reset");

        CustomItemManager customItemManager = plugin.getCustomItemManager();

        executesPlayer((player, args) -> {
            player.getInventory().clear();

            ItemStack item1 = customItemManager.getItem("firework_shotgun").getItem(player);
            ItemStack item2 = customItemManager.getItem("firework_shotgun_ammo").getItem(player, 64);

            ItemStack item3 = customItemManager.getItem("firework_rifle").getItem(player);
            ItemStack item4 = customItemManager.getItem("firework_rifle_ammo").getItem(player, 64);

//            ItemStack item5 = customItemManager.getItem("rocket_launcher").getItem(player);
            ItemStack item6 = customItemManager.getItem("rocket_launcher_ammo").getItem(player, 64);

            ItemStack item7 = customItemManager.getItem("player_compass").getItem(player);
            ItemStack item8 = customItemManager.getItem("throwable_tnt").getItem(player, 64);

            player.getInventory().addItem(item1, item2, item3, item4, item6, item7, item8);
            player.sendMessage(Component.text("Inventory reset!").color(NamedTextColor.GREEN));
        });

        register(plugin);
    }
}
