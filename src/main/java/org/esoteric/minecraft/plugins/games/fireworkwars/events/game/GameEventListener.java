package org.esoteric.minecraft.plugins.games.fireworkwars.events.game;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.esoteric.minecraft.plugins.games.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.games.fireworkwars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.games.fireworkwars.game.team.FireworkWarsTeam;
import org.esoteric.minecraft.plugins.games.fireworkwars.game.team.TeamPlayer;
import org.esoteric.minecraft.plugins.games.fireworkwars.language.LanguageManager;
import org.esoteric.minecraft.plugins.games.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.games.fireworkwars.scoreboard.wrapper.FireworkWarsScoreboard;
import org.esoteric.minecraft.plugins.games.fireworkwars.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.title.Title.title;

public class GameEventListener implements Listener {
    private final FireworkWarsPlugin plugin;
    private final LanguageManager languageManager;
    private final FireworkWarsGame game;

    private final Map<UUID, Pair<Double, Integer>> lastDamagePerPlayer;

    public GameEventListener(FireworkWarsPlugin plugin, FireworkWarsGame game) {
        this.plugin = plugin;
        this.languageManager = plugin.getLanguageManager();
        this.game = game;

        this.lastDamagePerPlayer = new HashMap<>();
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    @SuppressWarnings("UnstableApiUsage")
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!(event.getDamageSource().getCausingEntity() instanceof Player damager)) {
            return;
        }

        if (!game.isAlive(player) || !game.isAlive(damager)) {
            return;
        }

        if (damager.getUniqueId().equals(player.getUniqueId())) {
            return;
        }

        double finalDamage = event.getFinalDamage();
        int currentTick = plugin.getServer().getCurrentTick();

        Pair<Double, Integer> lastDamageInfo = lastDamagePerPlayer.getOrDefault(
            player.getUniqueId(), Pair.of(0.0D, currentTick));

        double lastDamage = lastDamageInfo.getLeft();
        int lastDamageTime = lastDamageInfo.getRight();

        if (finalDamage < lastDamage && currentTick - lastDamageTime < 10) {
            return;
        }

        TeamPlayer teamPlayer = TeamPlayer.from(damager);

        if (currentTick - lastDamageTime < 10) {
            teamPlayer.changeDamageDealt(-lastDamage);
        } else {
            teamPlayer.playSound(Sound.ENTITY_PLAYER_HURT);
        }

        teamPlayer.changeDamageDealt(Math.round(finalDamage));
        lastDamagePerPlayer.put(player.getUniqueId(), Pair.of(finalDamage, currentTick));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!game.isAlive(player)) {
            return;
        }

        if (player.getKiller() != null) {
            TeamPlayer.from(player.getKiller()).incrementKills();
        }

        event.setReviveHealth(20.0D);
        event.setCancelled(true);

        performDeath(player, event.deathMessage(), false);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void performDeath(Player player, Component deathMessage, boolean disconnected) {
        TeamPlayer teamPlayer = TeamPlayer.from(player);
        FireworkWarsTeam team = teamPlayer.getTeam();

        if (disconnected) {
            teamPlayer.unregister(true);
        }

        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        if (lastDamageCause != null && disconnected && lastDamageCause.getDamageSource().getCausingEntity() instanceof Player killer) {
            TeamPlayer killerTeamPlayer = TeamPlayer.from(killer);

            if (killerTeamPlayer != null && !team.equals(killerTeamPlayer.getTeam())) {
                killerTeamPlayer.incrementKills();
            }
        }

        player.setGameMode(GameMode.SPECTATOR);

        player.getInventory().forEach(drop -> {
            if (drop != null) {
                player.getWorld().dropItemNaturally(player.getLocation(), drop);
            }
        });
        player.getInventory().clear();

        Sound sound = disconnected
            ? Sound.ENTITY_LIGHTNING_BOLT_THUNDER
            : Sound.ENTITY_SKELETON_DEATH;

        game.getPlayers().forEach(tp -> tp.sendMessage(deathMessage));
        game.getPlayers().forEach(tp -> tp.playSound(sound));

        for (TeamPlayer tp : game.getPlayers()) {
            Player p = tp.getPlayer();
            FireworkWarsScoreboard scoreboard = tp.getScoreboard();

            Component teamName = team.getColoredTeamName();

            if (team.isEliminated()) {
                if (tp.getTeam().equals(team)) {
                    scoreboard.setTeamLine(
                        team, languageManager.getMessage(Message.SB_ELIMINATED_OWN_TEAM, p, teamName));
                } else {
                    scoreboard.setTeamLine(
                        team, languageManager.getMessage(Message.SB_ELIMINATED_TEAM, p, teamName));
                }
            } else {
                scoreboard.updateTeamLine(
                    team, Pair.of("%", team.getRemainingPlayers().size() + ""));
            }
        }

        boolean gameEnded = false;

        if (team.isEliminated()) {
            game.eliminateTeam(team);
            List<FireworkWarsTeam> remainingTeams = game.getRemainingTeams();

            if (remainingTeams.size() == 1) {
                game.preEndGame(remainingTeams.get(0));
                gameEnded = true;
            }
        }

        if (!gameEnded && !disconnected) {
            Title title = title(
                languageManager.getMessage(Message.YOU_DIED, player),
                languageManager.getMessage(Message.YOU_ARE_NOW_SPECTATOR, player));

            player.sendTitlePart(TitlePart.TITLE, title.title());
            player.sendTitlePart(TitlePart.SUBTITLE, title.subtitle());
        }
    }

    public void performDisconnectionDeath(Player player, Component displayName) {
        Component deathMessage = languageManager.getMessage(
            Message.PLAYER_KILLED_BY_DISCONNECTION, player, displayName);

        performDeath(player, deathMessage, true);
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!game.isAlive(player)) {
            return;
        }

        TeamPlayer teamPlayer = TeamPlayer.from(player);
        teamPlayer.showWorldBorder();
    }

    @EventHandler
    public void onPlayerSpawnChange(PlayerSetSpawnEvent event) {
        Player player = event.getPlayer();

        if (!game.isAlive(player)) {
            return;
        }

        event.setCancelled(true);
    }
}
