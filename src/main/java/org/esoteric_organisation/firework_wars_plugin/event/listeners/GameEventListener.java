package org.esoteric_organisation.firework_wars_plugin.event.listeners;

import org.bukkit.GameMode;
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
import org.esoteric_organisation.firework_wars_plugin.util.ReflectUtil;

import java.util.List;

public class GameEventListener implements Listener {
    private final FireworkWarsPlugin plugin;
    private final FireworkWarsGame game;

    private final ReflectUtil reflectUtil;

    public GameEventListener(FireworkWarsPlugin plugin, FireworkWarsGame game) {
        this.plugin = plugin;
        this.game = game;

        this.reflectUtil = new ReflectUtil();
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

        TeamPlayer.from(damager).addDamage((int) Math.round(event.getFinalDamage()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!game.isAlive(player)) {
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        plugin.runTaskLater(() -> player.spigot().respawn(), 1L);

        if (player.getKiller() != null) {
            TeamPlayer.from(player.getKiller()).incrementKills();
        }

        FireworkWarsTeam team = TeamPlayer.from(player.getUniqueId()).getTeam();

        if (game.isTeamEliminated(team)) {
            game.eliminateTeam(team);
            List<FireworkWarsTeam> remainingTeams = game.getRemainingTeams();

            if (remainingTeams.size() == 1) {
                game.preEndGame(remainingTeams.get(0));
            }
        }
    }
}
