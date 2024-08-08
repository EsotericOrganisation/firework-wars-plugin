package net.slqmy.firework_wars_plugin.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.slqmy.firework_wars_plugin.arena.Arena;

public class FireworkWarsGame {
  
  private Arena arena;

  private final int requiredPlayerCount = 2;

  private GameState gameState = GameState.WAITING;
  private List<Player> players = new ArrayList<>();

  public Arena getArena() {
    return arena;
  }

  public GameState getGameState() {
    return gameState;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  public FireworkWarsGame(Arena arena) {
    this.arena = arena;
  }

  public void addPlayer(Player player) {
    players.add(player);
    player.teleport(arena.getLobbySpawnLocation().getBukkitLocation());

    if (gameState == GameState.WAITING) {
      if (players.size() >= requiredPlayerCount) {
        startGame();
      }
    }
  }

  public void startGame() {
    GameCountdown countdown = new GameCountdown(this);
  }

  public enum GameState {
    WAITING,
    STARTING,
    PLAYING;
  }
}
