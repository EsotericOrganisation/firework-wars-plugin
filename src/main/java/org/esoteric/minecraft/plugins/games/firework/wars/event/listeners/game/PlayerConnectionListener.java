package org.esoteric.minecraft.plugins.games.firework.wars.event.listeners.game;

import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.esoteric.minecraft.plugins.games.firework.wars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.games.firework.wars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.games.firework.wars.game.team.TeamPlayer;

import java.util.Arrays;
import java.util.List;

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
        } else {
            game.getEventListener().onPlayerDeath(new PlayerDeathEvent(player, null, Arrays.asList(player.getInventory().getContents()), 0, Component.empty()));
            game.removePlayer(teamPlayer);
        }
    }
}
