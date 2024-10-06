package org.esoteric_organisation.firework_wars_plugin.game;

import org.bukkit.entity.Player;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.structure.Arena;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

  private final FireworkWarsPlugin plugin;

  private final Map<Arena, FireworkWarsGame> games = new HashMap<>();

  public GameManager(FireworkWarsPlugin plugin) {
    this.plugin = plugin;
  }

  public boolean hasOngoingGame(Arena arena) {
    return games.containsKey(arena);
  }

  public FireworkWarsGame getFireworkWarsGame(Arena arena) {
    FireworkWarsGame game = games.get(arena);

    if (game == null) {
      game = new FireworkWarsGame(plugin, arena);
      games.put(arena, game);
    }

    return games.get(arena);
  } 

  public FireworkWarsGame getFireworkWarsGame(Player player) {
    return games.values().stream()
        .filter(game -> game.containsPlayer(player))
        .findFirst()
        .orElse(null);
  }
}
