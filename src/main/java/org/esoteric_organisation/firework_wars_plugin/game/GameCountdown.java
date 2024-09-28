package org.esoteric_organisation.firework_wars_plugin.game;

import org.bukkit.scheduler.BukkitRunnable;

import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame.GameState;
import org.esoteric_organisation.firework_wars_plugin.language.Message;

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
    game.sendMessage(countDownSeconds == 1 ? Message.GAME_STARTING_IN_TIME_SINGULAR : Message.GAME_STARTING_IN_TIME_PLURAL, countDownSeconds);

    countDownSeconds--;
    if (countDownSeconds == 0) {
      cancel();
      game.startGame();
    }
  }
}
