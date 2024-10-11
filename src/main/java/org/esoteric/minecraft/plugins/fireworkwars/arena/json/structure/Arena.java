package org.esoteric.minecraft.plugins.fireworkwars.arena.json.structure;

import org.esoteric.minecraft.plugins.fireworkwars.arena.json.data.EndgameData;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.data.SupplyDropData;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.data.TeamData;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.components.ChestLocation;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.components.PlayerLocation;

import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public class Arena {
    private String[] worlds;

    private PlayerLocation waitingAreaLocation;

    private int minimumPlayerCount = 2;
    private int maximumPlayerCount = PlayerCount.INFINITY.getValue();

    private int countDownSeconds = 15;
    private double gameDurationMinutes = 30;

    private TeamData[] teamInformation;

    private ChestLocation[] chestLocations;

    private SupplyDropData supplyDropData;

    private EndgameData endgameData;

    public List<String> getWorlds() {
        return List.of(worlds);
    }

    public PlayerLocation getWaitingAreaLocation() {
        return waitingAreaLocation;
    }

    public int getMinimumPlayerCount() {
        return minimumPlayerCount;
    }

    public int getMaximumPlayerCount() {
        return maximumPlayerCount;
    }

    public int getCountDownSeconds() {
        return countDownSeconds;
    }

    public double getGameDurationMinutes() {
        return gameDurationMinutes;
    }

    public int getGameDurationTicks() {
        return (int) (gameDurationMinutes * 60 * 20);
    }

    public List<TeamData> getTeamInformation() {
        return List.of(teamInformation);
    }

    public List<ChestLocation> getChestLocations() {
        return List.of(chestLocations);
    }

    public SupplyDropData getSupplyDropData() {
        return supplyDropData;
    }

    public EndgameData getEndgameData() {
        return endgameData;
    }

    public double getEndgameDurationTicks() {
        return getGameDurationTicks() - endgameData.getEndgameStartTicks();
    }

    public enum PlayerCount {
        INFINITY(-1);

        private final int value;

        public int getValue() {
            return value;
        }

        PlayerCount(int value) {
            this.value = value;
        }
    }
}
