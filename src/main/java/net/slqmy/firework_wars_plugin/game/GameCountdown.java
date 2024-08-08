package net.slqmy.firework_wars_plugin.game;

import org.bukkit.scheduler.BukkitRunnable;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.game.FireworkWarsGame.GameState;

public class GameCountdown extends BukkitRunnable {

  private final FireworkWarsPlugin plugin;

  private final FireworkWarsGame game;

  public GameCountdown(FireworkWarsPlugin plugin, FireworkWarsGame game) {
    super();
    this.plugin = plugin;
    this.game = game;
  }

  public void start() {
    game.setGameState(GameState.STARTING);
    runTaskTimer(plugin, 0, 20);
  }

  @Override
  public void run() {
    
  }
}
