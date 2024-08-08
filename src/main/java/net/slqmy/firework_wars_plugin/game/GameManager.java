package net.slqmy.firework_wars_plugin.game;

import net.slqmy.firework_wars_plugin.game.FireworkWarsGame;
import net.slqmy.firework_wars_plugin.arena.Arena;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

  private Map<Arena, FireworkWarsGame> games = new HashMap<>();

  public GameManager() {}

  public FireworkWarsGame getFireworkWarsGame(Arena arena) {
    return games.get(arena);
  } 
}
