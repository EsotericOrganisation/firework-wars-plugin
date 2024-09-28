package org.esoteric_organisation.firework_wars_plugin.items.guns.shotgun;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsTeam;
import org.esoteric_organisation.firework_wars_plugin.items.guns.BaseGunItem;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.util.Vector;

import java.util.List;

public class FireworkShotgunItem extends BaseGunItem {
    public FireworkShotgunItem(FireworkWarsPlugin plugin) {
        super(plugin, "firework_shotgun", "firework_shotgun_ammo");
    }

    @Override
    public ItemStack getItem(Player player) {
        return getItemBuilder()
            .setName(languageManager.getMessage(Message.FIREWORK_SHOTGUN, player))
            .setLore(languageManager.getMessages(Message.FIREWORK_SHOTGUN_LORE, player))
            .itemSupplier(this::getCustomCrossbow)
            .modifyMeta(this::modifyMeta)
            .build();
    }

    @Override
    protected void onCrossbowLoad(Player player, FireworkWarsGame game, EntityLoadCrossbowEvent event) {
        FireworkWarsTeam team = game.getTeam(player);
        ItemStack firework = createFirework(team.getConfiguredTeam().getColor(), 5);

        CrossbowMeta crossbowMeta = (CrossbowMeta) event.getCrossbow().getItemMeta();

        crossbowMeta.setChargedProjectiles(List.of(firework, firework.clone(), firework.clone(), firework.clone(), firework.clone(), firework.clone(), firework.clone()));
        event.getCrossbow().setItemMeta(crossbowMeta);
    }

    @Override
    protected void onCrossbowShoot(Player player, FireworkWarsGame game, EntityShootBowEvent event) {
        if (!(event.getProjectile() instanceof Firework firework)) {
          return;
        }

        if (!getAmmoItem().isValidCustomItem(firework.getItem())) {
            return;
        }

        Vector newVelocity = firework.getVelocity().clone()
          .rotateAroundY(randomNumber(-Math.PI / 12.0D, Math.PI / 12.0D))
          .multiply(new Vector(1.0D, randomNumber(0.9D, 1.1D), 1.0D));

        firework.setVelocity(newVelocity);
        firework.setTicksToDetonate(Math.max(20, firework.getTicksToDetonate() - 5));
    }

    @EventHandler
    public void onFireworkExplode(FireworkExplodeEvent event) {

    }

    private double randomNumber(double start, double end) {
        return start + (Math.random() * (end - start));
    }
}
