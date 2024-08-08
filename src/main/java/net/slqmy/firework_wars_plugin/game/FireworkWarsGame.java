package net.slqmy.firework_wars_plugin.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.arena.Arena;
import net.slqmy.firework_wars_plugin.arena.ConfiguredTeam;
import net.slqmy.firework_wars_plugin.language.Message;

public class FireworkWarsGame {

  private final FireworkWarsPlugin plugin;

  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  private Arena arena;

  private final List<FireworkWarsTeam> teams = new ArrayList<>();

  private GameState gameState = GameState.WAITING;
  private List<Player> players = new ArrayList<>();

  public Arena getArena() {
    return arena;
  }

  public GameState getGameState() {
    return gameState;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  public FireworkWarsGame(FireworkWarsPlugin plugin, Arena arena) {
    this.plugin = plugin;
    this.arena = arena;
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
    for (ConfiguredTeam configuredTeam : arena.getTeamInformation()) {
      teams.add(new FireworkWarsTeam(configuredTeam));
    }

    distributePlayersAccrossTeams();
  }

  public void distributePlayersAccrossTeams() {
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
    if (isTeamElimated(team)) {
      elimenateTeam(team);
    }
  }

  public boolean isTeamElimated(FireworkWarsTeam team) {
    boolean isEveryoneInTeamElimenated = true;
    for (Player teamPlayer : team.getPlayers()) {
      if (teamPlayer.getGameMode() != GameMode.SPECTATOR) {
        isEveryoneInTeamElimenated = false;
        break;
      }
    }
    return isEveryoneInTeamElimenated;
  }

  public FireworkWarsTeam getTeam(Player player) {
    for (FireworkWarsTeam team : teams) {
      if (team.getPlayers().contains(player)) {
        return team;
      }
    }

    return null;
  }

  public void elimenateTeam(FireworkWarsTeam team) {
    sendMessage(Message.TEAM_ELIMENATED, miniMessage.deserialize(team.getConfiguredTeam().getMiniMessageString()));
  }

  public enum GameState {
    WAITING,
    STARTING,
    PLAYING;
  }
}
