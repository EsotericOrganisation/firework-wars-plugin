package org.esoteric_organisation.firework_wars_plugin.game.runnables;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder.EndgameData;
import org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder.SupplyDropData;
import org.esoteric_organisation.firework_wars_plugin.arena.json.structure.Arena;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame;
import org.esoteric_organisation.firework_wars_plugin.game.team.TeamPlayer;
import org.esoteric_organisation.firework_wars_plugin.language.LanguageManager;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.esoteric_organisation.firework_wars_plugin.scoreboard.wrapper.FireworkWarsScoreboard;
import org.esoteric_organisation.firework_wars_plugin.util.Util;

public class GameTickHandler extends BukkitRunnable {
    private final FireworkWarsPlugin plugin;
    private final LanguageManager languageManager;

    private final FireworkWarsGame game;
    private final Arena arena;

    private final SupplyDropData supplyDropData;
    private final EndgameData endgameData;

    private int ticksElapsed;
    private int ticksUntilSupplyDrop;

    private boolean endgameStarted;

    public GameTickHandler(FireworkWarsPlugin plugin, FireworkWarsGame game) {
        this.plugin = plugin;
        this.languageManager = plugin.getLanguageManager();

        this.game = game;
        this.arena = game.getArena();

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

        if (ticksUntilSupplyDrop-- <= 0) {
            handleSupplyDrops();
        }

        if (ticksElapsed >= endgameData.getEndgameStartTicks() && !endgameStarted) {
            startEndgame();
        }

        updateWoolColors();
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

    private void updateWoolColors() {
        game.getPlayers().forEach(TeamPlayer::correctWoolColors);
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

        if (minutes == 0) {
            return String.format("%02d", seconds) + (soon ? "s" : "");
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
        return ticks <= 10 * 20;
    }

    private void updateScoreboards() {
        for (TeamPlayer teamPlayer : game.getPlayers()) {
            FireworkWarsScoreboard scoreboard = teamPlayer.getScoreboard();
            Player player = teamPlayer.getPlayer();

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
