package net.slqmy.firework_wars_plugin.game;

import org.bukkit.scheduler.BukkitRunnable;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.game.FireworkWarsGame.GameState;
import net.slqmy.firework_wars_plugin.language.Message;

public class GameCountdown extends BukkitRunnable {

  private final FireworkWarsPlugin plugin;

  private final FireworkWarsGame game;

  private int countDownSeconds;

  public GameCountdown(FireworkWarsPlugin plugin, FireworkWarsGame game) {
    super();
    this.plugin = plugin;
    this.game = game;

    countDownSeconds = game.getArena().getCountDownSeconds();
    start();
  }

  public void start() {
    game.setGameState(GameState.STARTING);
    runTaskTimer(plugin, 0, 20);
  }

  @Override
  public void run() {
    game.sendMessage(Message.GAME_STARTING_IN_TIME, countDownSeconds);

    countDownSeconds--;
    if (countDownSeconds == 0) {
      cancel();
      game.startGame();
    }
  }
}
