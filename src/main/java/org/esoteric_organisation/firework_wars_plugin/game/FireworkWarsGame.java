package org.esoteric_organisation.firework_wars_plugin.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder.Arena;
import org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder.TeamData;
import org.esoteric_organisation.firework_wars_plugin.event.listeners.GameEventListener;
import org.esoteric_organisation.firework_wars_plugin.game.runnables.GameCountdown;
import org.esoteric_organisation.firework_wars_plugin.game.team.FireworkWarsTeam;
import org.esoteric_organisation.firework_wars_plugin.game.team.TeamPlayer;
import org.esoteric_organisation.firework_wars_plugin.language.Message;

import java.util.*;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class FireworkWarsGame {
    private final FireworkWarsPlugin plugin;

    private final Arena arena;
    private final GameEventListener eventListener;

    private GameState gameState = GameState.WAITING;
    private final Map<String, Boolean> worldLoadStates = new HashMap<>();

    private final List<FireworkWarsTeam> teams = new ArrayList<>();
    private final List<TeamPlayer> players = new ArrayList<>();

    public FireworkWarsPlugin getPlugin() {
        return plugin;
    }

    public Arena getArena() {
        return arena;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Map<String, Boolean> getWorldLoadStates() {
        return worldLoadStates;
    }

    public List<TeamPlayer> getPlayers() {
        return players;
    }

    public boolean isPlaying() {
        return gameState == GameState.PLAYING;
    }

    public boolean isWaiting() {
        return gameState == GameState.WAITING;
    }

    public boolean isStarting() {
        return gameState == GameState.STARTING;
    }

    public boolean isResetting() {
        return gameState == GameState.RESETTING;
    }

    public boolean isAlive(Player player) {
        return containsPlayer(player) && player.getGameMode() != GameMode.SPECTATOR;
    }

    public boolean isSpectator(Player player) {
        return !isAlive(player);
    }

    public boolean containsPlayer(Player player) {
        TeamPlayer teamPlayer = TeamPlayer.from(player.getUniqueId());
        return players.contains(teamPlayer);
    }

    public boolean usesWorld(String worldName) {
        return arena.getWorlds().contains(worldName);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public FireworkWarsGame(FireworkWarsPlugin plugin, Arena arena) {
        this.plugin = plugin;

        this.arena = arena;
        this.eventListener = new GameEventListener(plugin, this);

        for (String worldName : arena.getWorlds()) {
            worldLoadStates.put(worldName, true);
        }
    }

    public void addPlayer(Player player) {
        TeamPlayer teamPlayer = new TeamPlayer(player.getUniqueId(), this);

        players.add(teamPlayer);

        if (isWaiting() && players.size() >= arena.getMinimumPlayerCount()) {
            startCountdown();
        }

        teamPlayer.teleportToWaitingArea();
    }

    public void sendMessage(Message message, Object... arguments) {
        for (TeamPlayer player : players) {
            plugin.getLanguageManager().sendMessage(message, player.getPlayer(), arguments);
        }
    }

    private void startCountdown() {
        new GameCountdown(plugin, this);
    }

    public void startGame() {
        gameState = GameState.PLAYING;
        eventListener.register();

        for (TeamData configuredTeam : arena.getTeamInformation()) {
            teams.add(new FireworkWarsTeam(configuredTeam, plugin));
        }

        distributePlayersAcrossTeams();
    }

    public void preEndGame(FireworkWarsTeam winningTeam) {
        sendMessage(Message.TEAM_WON, winningTeam.getColoredTeamName());
        players.forEach(TeamPlayer::becomeSpectator);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> endGame(winningTeam), 20 * 10L);
    }

    public void endGame(FireworkWarsTeam winningTeam) {
        eventListener.unregister();

        players.forEach(TeamPlayer::teleportToLobby);
        players.forEach(player -> player.unregister(false));

        teams.clear();
        players.clear();

        gameState = GameState.RESETTING;
        plugin.getServer().getScheduler().runTaskLater(plugin, this::resetMap, 1L);
    }

    private void resetMap() {
        for (String worldName : arena.getWorlds()) {
            worldLoadStates.put(worldName, false);

            Bukkit.unloadWorld(worldName, false);
            World world = Bukkit.createWorld(new WorldCreator(worldName));

            assert world != null;
            world.setAutoSave(false);
        }
    }

    private void distributePlayersAcrossTeams() {
        for (int i = 0; i < players.size(); i++) {
            int teamIndex = i % teams.size();

            FireworkWarsTeam team = teams.get(teamIndex);
            team.addPlayer(players.get(i));
        }
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.SPECTATOR);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.spigot().respawn(), 1L);

        FireworkWarsTeam team = TeamPlayer.from(player.getUniqueId()).getTeam();

        if (isTeamEliminated(team)) {
            eliminateTeam(team);
            List<FireworkWarsTeam> remainingTeams = getRemainingTeams();

            if (remainingTeams.size() == 1) {
                preEndGame(remainingTeams.get(0));
            }
        }
    }

    private boolean isTeamEliminated(FireworkWarsTeam team) {
        return team.getPlayers().stream()
            .map(TeamPlayer::getPlayer)
            .filter(Objects::nonNull)
            .allMatch(this::isSpectator);
    }

    private List<FireworkWarsTeam> getRemainingTeams() {
        return teams
            .stream()
            .filter(team -> !isTeamEliminated(team))
            .toList();
    }

    private void eliminateTeam(FireworkWarsTeam team) {
        sendMessage(Message.TEAM_ELIMINATED, team.getColoredTeamName());
    }

    public enum GameState {
        WAITING,
        STARTING,
        PLAYING,
        RESETTING
    }
}
