package org.esoteric_organisation.firework_wars_plugin.event.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame;
import org.esoteric_organisation.firework_wars_plugin.game.team.FireworkWarsTeam;
import org.esoteric_organisation.firework_wars_plugin.game.team.TeamPlayer;
import org.esoteric_organisation.firework_wars_plugin.util.Pair;

import java.util.List;

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

        TeamPlayer.from(damager).addDamage(Math.round(event.getFinalDamage()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!game.isAlive(player)) {
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);

        Location location = player.getLocation();
        player.setRespawnLocation(location);

        plugin.runTaskLater(() -> player.spigot().respawn(), 1L);

        if (player.getKiller() != null) {
            TeamPlayer.from(player.getKiller()).incrementKills();
        }

        FireworkWarsTeam team = TeamPlayer.from(player.getUniqueId()).getTeam();

        for (TeamPlayer teamPlayer : game.getPlayers()) {
            teamPlayer.getScoreboard()
                .updateTeamLine(team, Pair.of("%", team.getRemainingPlayers().size() + ""))
                .update();
        }

        if (game.isTeamEliminated(team)) {
            game.eliminateTeam(team);
            List<FireworkWarsTeam> remainingTeams = game.getRemainingTeams();

            if (remainingTeams.size() == 1) {
                game.preEndGame(remainingTeams.get(0));
            }
        }
    }
}
