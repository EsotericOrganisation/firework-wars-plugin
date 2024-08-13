package net.slqmy.firework_wars_plugin.items.guns;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.game.FireworkWarsGame;
import net.slqmy.firework_wars_plugin.items.manager.AbstractItem;
import net.slqmy.firework_wars_plugin.items.nms.CustomCrossbow;
import net.slqmy.firework_wars_plugin.util.ItemBuilder;
import net.slqmy.firework_wars_plugin.util.Keys;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.FireworkMeta;

public abstract class BaseGunItem extends AbstractItem {
    protected final String ammoId;

    public BaseGunItem(FireworkWarsPlugin plugin, String itemId, String ammoId) {
        super(plugin, itemId, Material.CROSSBOW);

        this.ammoId = ammoId;
    }

    protected ItemBuilder<CrossbowMeta> getItemBuilder() {
        return new ItemBuilder<CrossbowMeta>(plugin, itemMaterial)
            .setEnchanted(true)
            .setUnbreakable(true);
    }

    protected ItemStack getCustomCrossbow() {
        CustomCrossbow crossbow = (CustomCrossbow) plugin.getCustomItemManager().getNMSItem("crossbow");
        ItemStack itemStack = crossbow.getDefaultInstance().asBukkitCopy();

        itemStack.setItemMeta(new ItemStack(Material.CROSSBOW).getItemMeta());
        return itemStack;
    }

    protected void modifyMeta(CrossbowMeta meta) {
        pdcManager.setStringValue(meta, isItemKey, itemId);
        pdcManager.setStringValue(meta, Keys.GUN_ACCEPTED_AMMO_ID, ammoId);
    }

    protected ItemStack createFirework(Color color, int stars) {
        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();

        addFireworkStars(fireworkMeta, color, stars);
        firework.setItemMeta(fireworkMeta);

        return firework;
    }

    protected void addFireworkStars(FireworkMeta meta, Color color, int amount) {
        for (int i = 0; i < amount; i++) {
            meta.addEffect(FireworkEffect
                .builder()
                .withColor(Color.WHITE)
                .withFade(color)
                .withFlicker()
                .build());
        }
    }

    protected abstract void onCrossbowLoad(Player player, FireworkWarsGame game, EntityLoadCrossbowEvent event);
    protected abstract void onCrossbowShoot(Player player, FireworkWarsGame game, EntityShootBowEvent event);

    @EventHandler
    public void onCrossbowLoad(EntityLoadCrossbowEvent event) {
        if (!isValidCustomItem(event.getCrossbow())) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        FireworkWarsGame game = plugin.getGameManager().getFireworkWarsGame(player);

        if (game == null) {
            return;
        }

        onCrossbowLoad(player, game, event);
    }

    @EventHandler
    public void onCrossbowShoot(EntityShootBowEvent event) {
        if (!isValidCustomItem(event.getBow())) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        FireworkWarsGame game = plugin.getGameManager().getFireworkWarsGame(player);

        if (game == null) {
            return;
        }

        onCrossbowShoot(player, game, event);
    }
}
