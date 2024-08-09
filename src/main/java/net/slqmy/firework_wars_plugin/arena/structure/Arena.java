package net.slqmy.firework_wars_plugin.arena.structure;

import com.google.gson.annotations.Expose;

import net.slqmy.firework_wars_plugin.arena.data_type.BlockLocation;
import net.slqmy.firework_wars_plugin.arena.data_type.PlayerLocation;

public class Arena {

  @Expose
  private PlayerLocation lobbySpawnLocation;
  @Expose
  private int minimumPlayerCount = 2;
  @Expose
  private int maximumPlayerCount = PlayerCount.INFINITY.getValue();
  @Expose
  private int countDownSeconds = 15;
  @Expose
  private ConfiguredTeam[] teamInformation;
  @Expose
  private BlockLocation[] chestLocations;

  public PlayerLocation getLobbySpawnLocation() {
    return lobbySpawnLocation;
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

  public ConfiguredTeam[] getTeamInformation() {
    return teamInformation;
  }

  public BlockLocation[] getChestLocations() {
    return chestLocations;
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
