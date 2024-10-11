package org.esoteric.minecraft.plugins.fireworkwars.commands;

import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.ServerOperator;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.items.CustomItemManager;
import org.jetbrains.annotations.NotNull;

public class ResetInventoryCommand extends CommandAPICommand {

    private final CustomItemManager manager;

    public ResetInventoryCommand(@NotNull FireworkWarsPlugin plugin) {
        super("reset");

        withRequirement(ServerOperator::isOp);

        this.manager = plugin.getCustomItemManager();

        executesPlayer((player, args) -> {
            giveItems(player);
        });

        register(plugin);
    }

    public void giveItems(@NotNull Player player) {
        player.getInventory().clear();

        ItemStack item1 = manager.getItem("firework_shotgun").getItem(player);
        ItemStack item2 = manager.getItem("firework_shotgun_ammo").getItem(player, 64);

        ItemStack item3 = manager.getItem("firework_rifle").getItem(player);
        ItemStack item4 = manager.getItem("firework_rifle_ammo").getItem(player, 64);

//            ItemStack item5 = manager.getItem("rocket_launcher").getItem(player);
        ItemStack item6 = manager.getItem("rocket_launcher_ammo").getItem(player, 64);

        ItemStack item7 = manager.getItem("player_compass").getItem(player);
        ItemStack item8 = manager.getItem("throwable_tnt").getItem(player, 64);

        player.getInventory().addItem(item3, item1, item8, item7, item6, item4, item2);
        player.sendMessage(Component.text("Inventory reset!").color(NamedTextColor.GREEN));
    }
}
