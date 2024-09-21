package net.slqmy.firework_wars_plugin.items.guns.rifle;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.game.FireworkWarsGame;
import net.slqmy.firework_wars_plugin.game.FireworkWarsTeam;
import net.slqmy.firework_wars_plugin.items.guns.BaseGunItem;
import net.slqmy.firework_wars_plugin.language.Message;
import org.bukkit.craftbukkit.entity.CraftFirework;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.function.Consumer;

public class FireworkRifleItem extends BaseGunItem {

    public FireworkRifleItem(FireworkWarsPlugin plugin) {
       super(plugin, "firework_rifle", "firework_rifle_ammo");
    }

    @Override
    public ItemStack getItem(Player player) {
        Consumer<CrossbowMeta> consumer = this::modifyMeta;
        Consumer<CrossbowMeta> modifyMeta = consumer.andThen(meta ->
            meta.addEnchant(Enchantment.QUICK_CHARGE, 3, true));

        return getItemBuilder()
            .setName(languageManager.getMessage(Message.FIREWORK_RIFLE, player))
            .setLore(languageManager.getMessages(Message.FIREWORK_RIFLE_LORE, player))
            .itemSupplier(this::getCustomCrossbow)
            .modifyMeta(modifyMeta)
            .build();
    }

    @Override
    protected void onCrossbowLoad(Player player, FireworkWarsGame game, EntityLoadCrossbowEvent event) {
        FireworkWarsTeam team = game.getTeam(player);
        ItemStack firework = createFirework(team.getConfiguredTeam().getColor(), 4);

        CrossbowMeta crossbowMeta = (CrossbowMeta) event.getCrossbow().getItemMeta();

        crossbowMeta.setChargedProjectiles(List.of(firework));
        event.getCrossbow().setItemMeta(crossbowMeta);
    }

    @Override
    protected void onCrossbowShoot(Player player, FireworkWarsGame game, EntityShootBowEvent event) {
        event.getProjectile().setNoPhysics(true);
        new FireworkRunnable((Firework) event.getProjectile()).runTaskTimer(plugin, 0L, 1L);
    }

    private static class FireworkRunnable extends BukkitRunnable {
        private final Firework firework;
        private final FireworkRocketEntity nmsFirework;

        private boolean shouldDetonate;
        private int ticksUntilDetonation;

        public FireworkRunnable(Firework firework) {
            this.firework = firework;
            this.nmsFirework = ((CraftFirework) firework).getHandle();
        }

        @Override
        public void run() {
            if (firework.isDetonated()) {
                cancel();
                return;
            }

            if (shouldDetonate) {
                if (ticksUntilDetonation-- <= 0) {
                    firework.detonate();
                    return;
                }
            }

            BlockPos position = new BlockPos(nmsFirework.getBlockX(), nmsFirework.getBlockY(), nmsFirework.getBlockZ());
            BlockState state = nmsFirework.level().getBlockState(position);

            if (nmsFirework.isColliding(position, state) || isCollidingWithAnyEntity()) {
                shouldDetonate = true;
                ticksUntilDetonation = (int) Math.round(Math.random() * 5);
            }
        }

        private boolean isCollidingWithAnyEntity() {
            Level world = nmsFirework.level();
            AABB entityBoundingBox = nmsFirework.getBoundingBox();

            for (Entity otherEntity : world.getEntities().getAll()) {
                if (otherEntity != nmsFirework) {
                    AABB otherEntityBoundingBox = otherEntity.getBoundingBox();

                    if (entityBoundingBox.intersects(otherEntityBoundingBox)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
