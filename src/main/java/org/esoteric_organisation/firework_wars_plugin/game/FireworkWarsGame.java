package org.esoteric_organisation.firework_wars_plugin.game;

import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.structure.Arena;
import org.esoteric_organisation.firework_wars_plugin.arena.structure.ConfiguredTeam;
import org.esoteric_organisation.firework_wars_plugin.event.listeners.GameEventListener;
import org.esoteric_organisation.firework_wars_plugin.game.runnables.GameCountdown;
import org.esoteric_organisation.firework_wars_plugin.game.team.FireworkWarsTeam;
import org.esoteric_organisation.firework_wars_plugin.game.team.TeamPlayer;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FireworkWarsGame {

  private final FireworkWarsPlugin plugin;

  private final Arena arena;
  private final GameEventListener eventListener;

  private GameState gameState = GameState.WAITING;

  private final List<FireworkWarsTeam> teams = new ArrayList<>();
  private final List<TeamPlayer> players = new ArrayList<>();

  public Arena getArena() {
    return arena;
  }

  public GameState getGameState() {
    return gameState;
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

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  public FireworkWarsGame(FireworkWarsPlugin plugin, Arena arena) {
    this.plugin = plugin;

    this.arena = arena;
    this.eventListener = new GameEventListener(plugin, this);
  }

  public void addPlayer(Player player) {
    TeamPlayer teamPlayer = new TeamPlayer(player.getUniqueId(), this);

    players.add(teamPlayer);

    if (isWaiting() && players.size() >= arena.getMinimumPlayerCount()) {
        startCountdown();
    }

    player.teleport(arena.getLobbySpawnLocation().getBukkitLocation());
  }

  public void sendMessage(Message message, Object... arguments) {
    for (TeamPlayer player : players) {
      plugin.getLanguageManager().sendMessage(message, player.getPlayer(), arguments);
    }
  }

  public void startCountdown() {
    new GameCountdown(plugin, this);
  }

  public void startGame() {
    gameState = GameState.PLAYING;
    eventListener.register();

    for (ConfiguredTeam configuredTeam : arena.getTeamInformation()) {
      teams.add(new FireworkWarsTeam(configuredTeam, plugin));
    }

    distributePlayersAcrossTeams();
  }

  public void endGame(FireworkWarsTeam winningTeam) {
    gameState = GameState.WAITING;
    eventListener.unregister();

    sendMessage(Message.TEAM_WON, winningTeam.getColoredTeamName());

    TeamPlayer.unregisterGame(this);

    teams.clear();
    players.clear();
  }

  public void distributePlayersAcrossTeams() {
    for (int i = 0; i < players.size(); i++) {
      int teamIndex = i % teams.size();

      FireworkWarsTeam team = teams.get(teamIndex);
      team.addPlayer(players.get(i));
    }
  }

  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getPlayer();
    player.setGameMode(GameMode.SPECTATOR);

    FireworkWarsTeam team = TeamPlayer.from(player.getUniqueId()).getTeam();

    if (isTeamEliminated(team)) {
      eliminateTeam(team);
      List<FireworkWarsTeam> remainingTeams = getRemainingTeams();

      if (remainingTeams.size() == 1) {
        endGame(remainingTeams.get(0));
      }
    }
  }

  public boolean isTeamEliminated(FireworkWarsTeam team) {
    return team
      .getPlayers()
      .stream()
      .map(TeamPlayer::getPlayer)
      .filter(Objects::nonNull)
      .allMatch(this::isSpectator);
  }

  public List<FireworkWarsTeam> getRemainingTeams() {
    return teams
      .stream()
      .filter(team -> !isTeamEliminated(team))
      .toList();
  }

  public void eliminateTeam(FireworkWarsTeam team) {
    sendMessage(Message.TEAM_ELIMINATED, team.getColoredTeamName());
  }

  public enum GameState {
    WAITING,
    STARTING,
    PLAYING;
  }
}
