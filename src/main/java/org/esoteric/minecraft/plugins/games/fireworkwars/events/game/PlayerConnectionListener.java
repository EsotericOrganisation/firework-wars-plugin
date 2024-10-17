package org.esoteric.minecraft.plugins.games.fireworkwars.events.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.esoteric.minecraft.plugins.games.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.games.fireworkwars.arena.manager.ArenaManager;
import org.esoteric.minecraft.plugins.games.fireworkwars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.games.fireworkwars.game.GameManager;
import org.esoteric.minecraft.plugins.games.fireworkwars.game.team.TeamPlayer;
import org.esoteric.minecraft.plugins.games.fireworkwars.language.LanguageManager;
import org.esoteric.minecraft.plugins.games.fireworkwars.language.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.title.Title.title;

public class PlayerConnectionListener implements Listener {
    private final FireworkWarsPlugin plugin;
    private final FireworkWarsGame game;

    private final GameManager gameManager;
    private final ArenaManager arenaManager;
    private final LanguageManager languageManager;

    private final List<UUID> disconnectedPlayers;

    public PlayerConnectionListener(FireworkWarsPlugin plugin, FireworkWarsGame game) {
        this.plugin = plugin;
        this.game = game;

        this.gameManager = plugin.getGameManager();
        this.arenaManager = plugin.getArenaManager();
        this.languageManager = plugin.getLanguageManager();

        this.disconnectedPlayers = new ArrayList<>();
    }

    public List<UUID> getDisconnectedPlayers() {
        return disconnectedPlayers;
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

        if (game.isWaiting() || game.isStarting()) {
            game.removePlayer(teamPlayer);
        } else if (game.isAlive(player)) {
            Component name = teamPlayer.getColoredName();

            game.sendMessage(Message.PLAYER_DISCONNECTED, name);
            game.getEventListener().performDisconnectionDeath(player, name);

            disconnectedPlayers.add(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();

        if (!game.equals(gameManager.getFireworkWarsGame(worldName))) {
            return;
        }

        switch (game.getGameState()) {
            case WAITING, STARTING ->
                game.addPlayer(player);
            case PLAYING -> {
                if (!disconnectedPlayers.contains(player.getUniqueId())) {
                    return;
                }

                Title title = title(
                    languageManager.getMessage(Message.YOU_DIED, player),
                    languageManager.getMessage(Message.YOU_ARE_NOW_SPECTATOR, player));

                player.sendTitlePart(TitlePart.TITLE, title.title());
                player.sendTitlePart(TitlePart.SUBTITLE, title.subtitle());

                player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_DEATH, 1.0F, 1.0F);
            }
            case RESETTING -> {
                Location location = arenaManager.getFirstLobbySpawnLocation();
                player.teleport(location);
                player.setGameMode(GameMode.ADVENTURE);
            }
        }
    }
}
