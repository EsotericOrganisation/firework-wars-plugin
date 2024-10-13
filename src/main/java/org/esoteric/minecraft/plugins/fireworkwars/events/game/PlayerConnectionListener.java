package org.esoteric.minecraft.plugins.fireworkwars.events.game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.TeamPlayer;

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
            } else if (game.isAlive(player)) {
                plugin.logLoudly("Player left bruv");
            }
        }

    }
}
