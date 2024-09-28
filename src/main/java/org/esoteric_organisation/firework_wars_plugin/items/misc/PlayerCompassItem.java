package org.esoteric_organisation.firework_wars_plugin.items.misc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsTeam;
import org.esoteric_organisation.firework_wars_plugin.items.manager.AbstractItem;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.esoteric_organisation.firework_wars_plugin.util.ItemBuilder;
import org.esoteric_organisation.firework_wars_plugin.util.Keys;
import org.esoteric_organisation.firework_wars_plugin.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Comparator.comparingDouble;

public class PlayerCompassItem extends AbstractItem {
    private static final Map<String, CompassUpdater> compassManagers = new HashMap<>();

    private final Message loreNotTracking = Message.PLAYER_COMPASS_LORE_NOT_TRACKING;
    private final Message loreTracking = Message.PLAYER_COMPASS_LORE_TRACKING;
    private final Message noEnemy = Message.PLAYER_COMPASS_NO_ENEMY;
    private final Message trackingTarget = Message.PLAYER_COMPASS_TRACKING_TARGET;
    private final Message actionBarInfo = Message.PLAYER_COMPASS_ACTIONBAR_INFO;

    public PlayerCompassItem(FireworkWarsPlugin plugin) {
        super(plugin, "player_compass", Material.COMPASS);
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder<CompassMeta>(plugin, itemMaterial)
            .setName(languageManager.getMessage(Message.PLAYER_COMPASS, player))
            .setLore(languageManager.getMessages(loreNotTracking, player))
            .modifyMeta(this::modifyMeta)
            .build();
    }

    private void modifyMeta(CompassMeta meta) {
        pdcManager.setStringValue(meta, isItemKey, itemId);
        pdcManager.setStringValue(meta, Keys.PLAYER_COMPASS_ID, UUID.randomUUID().toString());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!isValidCustomItem(item)) {
            return;
        }

        FireworkWarsGame game = plugin.getGameManager().getFireworkWarsGame(player);

        if (game == null || !game.isPlaying()) {
            return;
        }

        FireworkWarsTeam team = game.getTeam(player);
        Location playerLocation = player.getLocation();

        Player nearestEnemy = game.getPlayers()
            .stream()
            .filter(gamePlayer -> !game.getTeam(gamePlayer).equals(team))
            .min(comparingDouble(enemy -> enemy.getLocation().distanceSquared(playerLocation)))
            .orElse(null);

        String compassId = pdcManager.getStringValue(item.getItemMeta(), Keys.PLAYER_COMPASS_ID);
        CompassUpdater compassUpdater = compassManagers.computeIfAbsent(
            compassId, key -> new CompassUpdater(game, player, item));

        if (nearestEnemy == null) {
            player.sendMessage(languageManager.getMessage(noEnemy, player));
            compassUpdater.setTarget(null);
        } else {
            compassUpdater.setTarget(nearestEnemy);
        }

        compassUpdater.runTaskTimer(plugin, 0L, 1L);
    }

    private class CompassUpdater extends BukkitRunnable {
        private final FireworkWarsGame game;
        private final String compassId;
        private final Player player;
        private final ItemStack compass;

        private Player target;

        public void setTarget(Player target) {
            this.target = target;

            if (target != null) {
                Util.playSound(player, Sound.BLOCK_BEACON_POWER_SELECT);

                compass.lore(List.of(languageManager.getMessage(loreTracking, player, target.displayName())));
                player.sendMessage(languageManager.getMessage(trackingTarget, player, target.displayName()));
            } else {
                Util.playSound(player, Sound.ENTITY_ENDER_EYE_DEATH);

                compass.lore(List.of(languageManager.getMessage(loreNotTracking, player)));
            }
        }

        public CompassUpdater(FireworkWarsGame game, Player player, ItemStack compass) {
            this.game = game;
            this.compassId = pdcManager.getStringValue(compass.getItemMeta(), Keys.PLAYER_COMPASS_ID);
            this.player = player;
            this.compass = compass;
        }

        @Override
        public void run() {
            if (!game.isPlaying() || game.isSpectator(player) || !hasExactCompass()) {
                cancel();
                return;
            }

            if (target == null || !player.isOnline()) {
                return;
            }

            if (!target.isOnline() || game.isSpectator(target)) {
                setTarget(null);
                return;
            }

            player.setCompassTarget(target.getLocation());

            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();

            if (mainHand.equals(compass) || offHand.equals(compass)) {
                int distance = (int) player.getLocation().distance(target.getLocation());

                player.sendActionBar(languageManager.getMessage(
                    actionBarInfo, player, target.displayName(), Component.text(distance + "m").color(NamedTextColor.AQUA)));
            }
        }

        @Override
        public void cancel() {
            super.cancel();
            compassManagers.remove(pdcManager.getStringValue(compass.getItemMeta(), Keys.PLAYER_COMPASS_ID));
        }

        private String wrapInAqua(Player player) {
            return "<aqua>" + player.getName() + "</aqua>";
        }

      private String wrapInAqua(String text) {
        return "<aqua>" + text + "</aqua>";
      }

        private boolean hasExactCompass() {
            return Util.testInventory(player.getInventory(), item ->
              pdcManager.getStringValue(item.getItemMeta(), Keys.PLAYER_COMPASS_ID).equals(compassId));
        }
    }
}
