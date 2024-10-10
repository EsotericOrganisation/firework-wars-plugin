package org.esoteric_organisation.firework_wars_plugin.items.explosives;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.AbstractItem;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.esoteric_organisation.firework_wars_plugin.util.ItemBuilder;
import org.esoteric_organisation.firework_wars_plugin.util.Util;

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

        tnt.setVelocity(player.getLocation().getDirection().add(velocity));

        event.setCancelled(true);
    }

    @Override
    public int getStackAmount() {
        return Util.randomInt(3, 4);
    }
}
