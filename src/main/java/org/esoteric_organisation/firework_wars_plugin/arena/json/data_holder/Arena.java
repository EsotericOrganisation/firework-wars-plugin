package org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder;

import org.esoteric_organisation.firework_wars_plugin.arena.json.mini_components.BlockLocation;
import org.esoteric_organisation.firework_wars_plugin.arena.json.mini_components.PlayerLocation;

import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public class Arena {
  private String[] worlds;

  private PlayerLocation waitingAreaLocation;

  private int minimumPlayerCount = 2;
  private int maximumPlayerCount = PlayerCount.INFINITY.getValue();

  private int countDownSeconds = 15;

  private TeamData[] teamInformation;

  private BlockLocation[] chestLocations;

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

  public List<TeamData> getTeamInformation() {
    return List.of(teamInformation);
  }

  public List<BlockLocation> getChestLocations() {
    return List.of(chestLocations);
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
