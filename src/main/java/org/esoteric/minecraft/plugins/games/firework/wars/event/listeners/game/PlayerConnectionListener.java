package org.esoteric.minecraft.plugins.games.firework.wars.event.listeners.game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.esoteric.minecraft.plugins.games.firework.wars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.games.firework.wars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.games.firework.wars.game.team.TeamPlayer;

public class PlayerConnectionListener implements Listener {
    private final FireworkWarsPlugin plugin;
    private final FireworkWarsGame game;

    public PlayerConnectionListener(FireworkWarsPlugin plugin, FireworkWarsGame game) {
        this.plugin = plugin;
        this.game = game;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TeamPlayer teamPlayer = TeamPlayer.from(player.getUniqueId());

        if (teamPlayer == null) {
            return;
        }

        if (teamPlayer.getGame().equals(game)) {
            if (game.isWaiting() || game.isStarting()) {
                game.removePlayer(teamPlayer);
            }
        }
    }
}
