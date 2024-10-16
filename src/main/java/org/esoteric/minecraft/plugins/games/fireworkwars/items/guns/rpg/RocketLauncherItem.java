package org.esoteric.minecraft.plugins.games.fireworkwars.items.guns.rpg;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.Color;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.esoteric.minecraft.plugins.games.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.games.fireworkwars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.games.fireworkwars.items.guns.BaseGunItem;
import org.esoteric.minecraft.plugins.games.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.games.fireworkwars.util.Keys;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RocketLauncherItem extends BaseGunItem {

    public RocketLauncherItem(FireworkWarsPlugin plugin) {
        super(plugin, "rocket_launcher", "rocket_launcher_ammo", 10, 35);
    }

    @Override
    protected void onCrossbowLoad(Player player, FireworkWarsGame game, EntityLoadCrossbowEvent event) {
        editCrossbowMeta(event.getCrossbow(), meta -> meta
            .setChargedProjectiles(List.of(createFirework(Color.RED, 1, 2))));
    }

    @Override
    protected void onCrossbowShoot(Player player, FireworkWarsGame game, EntityShootBowEvent event) {

    }

    @Override
    public ItemStack getItem(Player player) {
        return getBaseCrossbowBuilder()
            .setName(Message.ROCKET_LAUNCHER, player)
            .setLore(Message.ROCKET_LAUNCHER_LORE, player)
            .build();
    }

    @EventHandler
    public void onFireworkExplode(@NotNull FireworkExplodeEvent event) {
        Firework firework = event.getEntity();

        if (!(firework.getShooter() instanceof Player player)) {
            return;
        }

        FireworkWarsGame game = plugin.getGameManager().getFireworkWarsGame(player);

        if (game == null || !game.isPlaying()) {
            return;
        }

        String value = pdcManager.getStringValue(firework.getFireworkMeta(), Keys.CUSTOM_ITEM_ID);
        boolean isRocket = ammoId.equals(value);

        if (isRocket) {
            firework.getWorld().createExplosion(
                    firework, firework.getLocation(), 2.0F, false, true);

            firework.remove();
            event.setCancelled(true);
        }
    }

    @Override
    public int getStackAmount() {
        return 1;
    }
}
