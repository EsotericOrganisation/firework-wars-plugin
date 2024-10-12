package org.esoteric.minecraft.plugins.fireworkwars.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.FireworkWarsTeam;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.TeamPlayer;
import org.esoteric.minecraft.plugins.fireworkwars.util.Pair;

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

        Bukkit.broadcastMessage(event.getFinalDamage() + "");

        TeamPlayer.from(damager).addDamage(Math.round(event.getFinalDamage()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!game.isAlive(player)) {
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);

        if (player.getKiller() != null) {
            TeamPlayer.from(player.getKiller()).incrementKills();
        }

        FireworkWarsTeam team = TeamPlayer.from(player.getUniqueId()).getTeam();

        game.getPlayers().forEach(teamPlayer ->
            teamPlayer.getScoreboard()
                .updateTeamLine(team, Pair.of("%", team.getRemainingPlayers().size() + ""))
                .update());

        if (game.isTeamEliminated(team)) {
            game.eliminateTeam(team);
            List<FireworkWarsTeam> remainingTeams = game.getRemainingTeams();

            if (remainingTeams.size() == 1) {
                game.preEndGame(remainingTeams.get(0));
            }
        }

        game.sendMessage(event.deathMessage());
        game.playSound(event.getDeathSound());

        event.getPlayer().getInventory().clear();
        event.getDrops().forEach(drop -> player.getWorld().dropItemNaturally(player.getLocation(), drop));

        event.setReviveHealth(20.0D);
        event.setCancelled(true);
    }
}
