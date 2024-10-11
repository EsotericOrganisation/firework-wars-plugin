package org.esoteric.minecraft.plugins.fireworkwars.items.explosives;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.items.AbstractItem;
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.fireworkwars.util.ItemBuilder;
import org.esoteric.minecraft.plugins.fireworkwars.util.Util;

public class ThrowableTNTItem extends AbstractItem {
    public ThrowableTNTItem(FireworkWarsPlugin plugin) {
        super(plugin, "throwable_tnt", Material.TNT, 4, 9);
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder<>(plugin, itemMaterial)
                .setName(Message.THROWABLE_TNT, player)
                .setLore(Message.THROWABLE_TNT_LORE, player)
                .setEnchanted(true)
                .modifyMeta(meta -> pdcManager.setStringValue(meta, customItemIdKey, itemId))
                .build();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (!event.getAction().isRightClick()) {
            return;
        }

        if (!isValidCustomItem(item)) {
            return;
        }

        item.setAmount(item.getAmount() - 1);

        TNTPrimed tnt = player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);
        tnt.setSource(player);

        tnt.setFuseTicks(50);

        CraftPlayer craftPlayer = (CraftPlayer) player;

        ServerPlayer serverPlayer = craftPlayer.getHandle();
        double lastX = serverPlayer.xo;
        double lastY = serverPlayer.yo;
        double lastZ = serverPlayer.zo;

        Vector velocity = new Vector(
                player.getX() - lastX,
                player.getY() - lastY,
                player.getZ() - lastZ
        );

        Bukkit.broadcastMessage(velocity.toString());

        Vector playerDirection = player.getLocation().getDirection().clone();
        Vector playerDirectionWithVelocity = playerDirection.clone().add(velocity);

        Bukkit.broadcastMessage(playerDirectionWithVelocity.toString());

        tnt.setVelocity(playerDirectionWithVelocity);

        event.setCancelled(true);
    }

    @Override
    public int getStackAmount() {
        return Util.randomInt(3, 4);
    }
}
