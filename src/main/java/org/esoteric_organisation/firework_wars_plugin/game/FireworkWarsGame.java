package org.esoteric_organisation.firework_wars_plugin.game;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.structure.Arena;
import org.esoteric_organisation.firework_wars_plugin.arena.structure.ConfiguredTeam;
import org.esoteric_organisation.firework_wars_plugin.event.listeners.GameEventListener;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class FireworkWarsGame {

  private final FireworkWarsPlugin plugin;

  private final Arena arena;
  private final GameEventListener eventListener;

  private GameState gameState = GameState.WAITING;

  private final List<FireworkWarsTeam> teams = new ArrayList<>();
  private final List<Player> players = new ArrayList<>();

  public Arena getArena() {
    return arena;
  }

  public GameState getGameState() {
    return gameState;
  }

  public List<Player> getPlayers() {
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
    return players.contains(player) && player.getGameMode() != GameMode.SPECTATOR;
  }

  public boolean isSpectator(Player player) {
    return players.contains(player) && player.getGameMode() == GameMode.SPECTATOR;
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
    players.add(player);
    player.teleport(arena.getLobbySpawnLocation().getBukkitLocation());

    if (gameState == GameState.WAITING) {
      if (players.size() >= arena.getMinimumPlayerCount()) {
        startCountdown();
      }
    }
  }

  public void sendMessage(Message message, Object... arguments) {
    for (Player player : players) {
      plugin.getLanguageManager().sendMessage(message, player, arguments);
    }
  }

  public void startCountdown() {
    new GameCountdown(plugin, this);
  }

  public void startGame() {
    gameState = GameState.PLAYING;
    eventListener.register();

    for (ConfiguredTeam configuredTeam : arena.getTeamInformation()) {
      teams.add(new FireworkWarsTeam(configuredTeam));
    }

    distributePlayersAcrossTeams();
  }

  public void endGame(FireworkWarsTeam winningTeam) {
    gameState = GameState.WAITING;
    eventListener.unregister();

    teams.clear();
    players.clear();

    sendMessage(Message.TEAM_WON, winningTeam.getDeserializedTeamName());
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

    FireworkWarsTeam team = getTeam(player);

    if (isTeamEliminated(team)) {
      eliminateTeam(team);
      List<FireworkWarsTeam> remainingTeams = getRemainingTeams();

      Bukkit.broadcastMessage("Remaining teams: " + remainingTeams.size());

      if (remainingTeams.size() == 1) {
        endGame(remainingTeams.get(0));
      }
    }
  }

  public boolean isTeamEliminated(FireworkWarsTeam team) {
    return team
      .getPlayers()
      .stream()
      .allMatch(player -> player.getGameMode() == GameMode.SPECTATOR);
  }

  public List<FireworkWarsTeam> getRemainingTeams() {
    return teams
      .stream()
      .filter(team -> !isTeamEliminated(team))
      .toList();
  }

  public FireworkWarsTeam getTeam(Player player) {
    return teams
      .stream()
      .filter(team -> team.getPlayers().contains(player))
      .findFirst()
      .orElse(null);
  }

  public void eliminateTeam(FireworkWarsTeam team) {
    sendMessage(Message.TEAM_ELIMINATED, team.getDeserializedTeamName());
  }

  public enum GameState {
    WAITING,
    STARTING,
    PLAYING;
  }
}
