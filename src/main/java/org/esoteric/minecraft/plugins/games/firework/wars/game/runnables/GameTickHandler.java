package org.esoteric.minecraft.plugins.games.firework.wars.game.runnables;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.esoteric.minecraft.plugins.games.firework.wars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.games.firework.wars.arena.json.data.EndgameData;
import org.esoteric.minecraft.plugins.games.firework.wars.arena.json.data.SupplyDropData;
import org.esoteric.minecraft.plugins.games.firework.wars.arena.json.structure.Arena;
import org.esoteric.minecraft.plugins.games.firework.wars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.games.firework.wars.game.team.TeamPlayer;
import org.esoteric.minecraft.plugins.games.firework.wars.language.LanguageManager;
import org.esoteric.minecraft.plugins.games.firework.wars.language.Message;
import org.esoteric.minecraft.plugins.games.firework.wars.scoreboard.wrapper.FireworkWarsScoreboard;
import org.esoteric.minecraft.plugins.games.firework.wars.util.Pair;
import org.esoteric.minecraft.plugins.games.firework.wars.util.Util;

public class GameTickHandler extends BukkitRunnable {
    private final FireworkWarsPlugin plugin;
    private final LanguageManager languageManager;

    private final FireworkWarsGame game;
    private final Arena arena;

    private final SupplyDropData supplyDropData;
    private final EndgameData endgameData;

    private int ticksElapsed;
    private int ticksUntilSupplyDrop;

    private final int chestRefillInterval;
    private int totalChestRefills;

    private boolean endgameStarted;

    public GameTickHandler(FireworkWarsPlugin plugin, FireworkWarsGame game) {
        this.plugin = plugin;
        this.languageManager = plugin.getLanguageManager();

        this.game = game;
        this.arena = game.getArena();

        this.chestRefillInterval = arena.getChestRefillIntervalTicks();
        this.totalChestRefills = 0;

        this.supplyDropData = arena.getSupplyDropData();
        this.endgameData = arena.getEndgameData();
    }

    public void start() {
        init();
        runTaskTimer(plugin, 1L, 1L);
    }

    @Override
    public void run() {
        ticksElapsed++;

        if (--ticksUntilSupplyDrop <= 20) {
            handleSupplyDrops();
        }

        if (ticksElapsed >= endgameData.getEndgameStartTicks() - 20 && !endgameStarted) {
            startEndgame();
        }

        if (ticksElapsed >= arena.getGameDurationTicks()) {
            game.preEndGame();
            return;
        }

        if (totalChestRefills < 10) {
            if (ticksElapsed % chestRefillInterval == chestRefillInterval - 11 * 20) {
                game.sendMessage(Message.EVENT_CHEST_REFILL_WARNING, 10);
            }

            if (ticksElapsed % chestRefillInterval == 0) {
                handleChestRefill();
            }
        }

        updateScoreboards();
    }

    private void init() {
        ticksElapsed = 0;
        ticksUntilSupplyDrop = getNextSupplyDropTicks();
    }

    private int getNextSupplyDropTicks() {
        int interval = supplyDropData.getSupplyDropIntervalTicks();
        int randomness = supplyDropData.getSupplyDropIntervalRandomness();

        return Math.max(1, interval + Util.randomInt(-randomness, randomness));
    }

    private void handleChestRefill() {
        totalChestRefills++;
        game.getChestManager().refillChests(1.0D + totalChestRefills / 10.0D);

        game.sendMessage(Message.EVENT_CHEST_REFILL);
        game.playSound(Sound.BLOCK_CHEST_OPEN);
    }

    private void handleSupplyDrops() {
        game.supplyDrop();
        ticksUntilSupplyDrop = getNextSupplyDropTicks();
    }

    private void startEndgame() {
        endgameStarted = true;
        game.startEndgame();
    }

    private String getMinutesAndSeconds(int ticks) {
        int minutes = ticks / 1200;
        int seconds = (ticks % 1200) / 20;
        boolean soon = startsSoon(ticks);

        if (minutes == 0 && soon) {
            return String.format("%02d", seconds) + "s";
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private boolean shouldWarnForEndgame() {
        return !endgameStarted && getTicksUntilEndgame() < endgameData.getWarnBeforeEndgameTicks();
    }

    private boolean shouldWarnForGameEnd() {
        return endgameStarted && getTicksUntilGameEnd() < endgameData.getWarnBeforeGameEndTicks();
    }

    private int getTicksUntilEndgame() {
        return endgameData.getEndgameStartTicks() - ticksElapsed;
    }

    private int getTicksUntilGameEnd() {
        return arena.getGameDurationTicks() - ticksElapsed;
    }

    private boolean startsSoon(int ticks) {
        return ticks < 11 * 20;
    }

    private void updateScoreboards() {
        for (TeamPlayer teamPlayer : game.getPlayers()) {
            FireworkWarsScoreboard scoreboard = teamPlayer.getScoreboard();
            Player player = teamPlayer.getPlayer();

            scoreboard.updateLine(4, Pair.of("%", teamPlayer.getKills() + ""));
            scoreboard.updateLine(5, Pair.of("%", (int) (teamPlayer.getDamage()) + ""));

            scoreboard.setIncludeSecondEventLine(false);

            if (startsSoon(ticksUntilSupplyDrop)) {
                scoreboard.setLine(1, languageManager.getMessage(
                        Message.SB_EVENT_SUPPLY_DROP_SOON, player, getMinutesAndSeconds(ticksUntilSupplyDrop)));
            } else {
                scoreboard.setLine(1, languageManager.getMessage(
                        Message.SB_EVENT_SUPPLY_DROP, player, getMinutesAndSeconds(ticksUntilSupplyDrop)));
            }

            if (shouldWarnForEndgame()) {
                scoreboard.setIncludeSecondEventLine(true);
                int ticks = getTicksUntilEndgame();

                if (startsSoon(ticks)) {
                    scoreboard.setEndgameLine(languageManager.getMessage(
                            Message.SB_EVENT_ENDGAME_SOON, player, getMinutesAndSeconds(getTicksUntilEndgame())));
                } else {
                    scoreboard.setEndgameLine(languageManager.getMessage(
                            Message.SB_EVENT_ENDGAME, player, getMinutesAndSeconds(getTicksUntilEndgame())));
                }
            }

            if (shouldWarnForGameEnd()) {
                scoreboard.setIncludeSecondEventLine(true);
                int ticks = getTicksUntilGameEnd();

                if (startsSoon(ticks)) {
                    scoreboard.setEndgameLine(languageManager.getMessage(
                            Message.SB_EVENT_GAME_END_SOON, player, getMinutesAndSeconds(getTicksUntilGameEnd())));
                } else {
                    scoreboard.setEndgameLine(languageManager.getMessage(
                            Message.SB_EVENT_GAME_END, player, getMinutesAndSeconds(getTicksUntilGameEnd())));
                }
            }

            scoreboard.update();
        }
    }
}
