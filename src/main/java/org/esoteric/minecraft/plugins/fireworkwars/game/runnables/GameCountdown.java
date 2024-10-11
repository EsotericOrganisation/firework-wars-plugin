package org.esoteric.minecraft.plugins.fireworkwars.game.runnables;

import org.bukkit.scheduler.BukkitRunnable;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.fireworkwars.game.FireworkWarsGame.GameState;
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;

public class GameCountdown extends BukkitRunnable {

    private final FireworkWarsPlugin plugin;
    private final FireworkWarsGame game;

    private int countDownSeconds;

    public GameCountdown(FireworkWarsPlugin plugin, FireworkWarsGame game) {
        super();
        this.plugin = plugin;
        this.game = game;

        this.countDownSeconds = game.getArena().getCountDownSeconds();
        start();
    }

    public void start() {
        game.setGameState(GameState.STARTING);
        runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void run() {
        game.sendMessage(countDownSeconds == 1 ? Message.GAME_STARTING_IN_TIME_SINGULAR : Message.GAME_STARTING_IN_TIME_PLURAL, countDownSeconds);

        if (--countDownSeconds == 0) {
            cancel();
            game.startGame();
        }
    }
}
