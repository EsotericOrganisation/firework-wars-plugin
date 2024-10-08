package org.esoteric_organisation.firework_wars_plugin.event.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame;

public class GameEventListener implements Listener {

    private final FireworkWarsPlugin plugin;
    private final FireworkWarsGame game;

    public GameEventListener(FireworkWarsPlugin plugin, FireworkWarsGame game) {
        this.plugin = plugin;
        this.game = game;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!game.isAlive(player)) {
            return;
        }

        game.onPlayerDeath(event);
    }
}
