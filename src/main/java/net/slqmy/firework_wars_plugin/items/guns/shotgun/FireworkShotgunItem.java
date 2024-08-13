package net.slqmy.firework_wars_plugin.items.guns.shotgun;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.game.FireworkWarsGame;
import net.slqmy.firework_wars_plugin.game.FireworkWarsTeam;
import net.slqmy.firework_wars_plugin.items.guns.BaseGunItem;
import net.slqmy.firework_wars_plugin.language.Message;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
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
            .setName(plugin.getLanguageManager().getMessage(Message.FIREWORK_SHOTGUN, player))
            .setLore(plugin.getLanguageManager().getMessage(Message.FIREWORK_SHOTGUN_LORE, player))
            .itemSupplier(this::getCustomCrossbow)
            .modifyMeta(this::modifyMeta)
            .build();
    }

    @Override
    protected void onCrossbowLoad(Player player, FireworkWarsGame game, EntityLoadCrossbowEvent event) {
        FireworkWarsTeam team = game.getTeam(player);

        ItemStack firework = createFirework(team.getConfiguredTeam().getColor(), 2);

        CrossbowMeta crossbowMeta = (CrossbowMeta) event.getCrossbow().getItemMeta();
        crossbowMeta.setChargedProjectiles(List.of(firework));
    }

    @Override
    protected void onCrossbowShoot(Player player, FireworkWarsGame game, EntityShootBowEvent event) {
        ItemStack firework = ((Firework) event.getProjectile()).getItem();
        Vector velocity = event.getProjectile().getVelocity();

        for (int i = 0; i < 3; i++) {
            Vector deviated = velocity.clone().add(
                new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5));

            player.launchProjectile(Firework.class, deviated, (fw) -> {
                fw.setItem(firework.clone());
            });
        }
    }
}
