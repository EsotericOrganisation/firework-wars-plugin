package org.esoteric.minecraft.plugins.fireworkwars.events.game;

import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
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
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.fireworkwars.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameEventListener implements Listener {
    private final FireworkWarsPlugin plugin;
    private final FireworkWarsGame game;

    private final Map<UUID, Pair<Double, Integer>> lastDamagePerPlayer;

    public GameEventListener(FireworkWarsPlugin plugin, FireworkWarsGame game) {
        this.plugin = plugin;
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

        plugin.logLoudly("Last damage: " + lastDamage);
        plugin.logLoudly("Current damage: " + finalDamage);
        plugin.logLoudly("Last damage tick: " + lastDamageTime);
        plugin.logLoudly("Current damage tick: " + currentTick);

        if (finalDamage < lastDamage && currentTick - lastDamageTime < 10) {
            plugin.logLoudly("Damage cancelled");
            return;
        }

        TeamPlayer.from(damager).addDamage(Math.round(finalDamage));
        lastDamagePerPlayer.put(player.getUniqueId(), Pair.of(finalDamage, currentTick));
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

        Title title = Title.title(plugin.getLanguageManager().getMessage(Message.YOU_DIED, event.getPlayer()), plugin.getLanguageManager().getMessage(Message.YOU_ARE_NOW_SPECTATOR, event.getPlayer()));
        event.getPlayer().sendTitlePart(TitlePart.TITLE, title.title());
        event.getPlayer().sendTitlePart(TitlePart.SUBTITLE, title.subtitle());
    }
}
