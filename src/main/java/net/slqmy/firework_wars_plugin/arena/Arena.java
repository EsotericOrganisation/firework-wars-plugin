package net.slqmy.firework_wars_plugin.arena;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.Expose;

import net.slqmy.firework_wars_plugin.arena.BlockLocation;
import net.slqmy.firework_wars_plugin.arena.PlayerLocation;
import net.slqmy.firework_wars_plugin.arena.Team;

public class Arena {

  @Expose
  private PlayerLocation lobbySpawnLocation;
  @Expose
  private @Nullable int minimumPlayerCount;
  @Expose
  private @Nullable int maximumPlayerCount;
  @Expose
  private int countDownSeconds = 15;
  @Expose
  private Team[] teamInformation;
  @Expose
  private BlockLocation[] chestLocations;

  public PlayerLocation getLobbySpawnLocation() {
    return lobbySpawnLocation;
  }

  public @Nullable int getMinimumPlayerCount() {
    return minimumPlayerCount;
  }

  public @Nullable int getmaximumPlayerCount() {
    return maximumPlayerCount;
  }

  public int getCountDownSeconds() {
    return countDownSeconds;
  }

  public Team[] getTeamInformation() {
    return teamInformation;
  }

  public BlockLocation[] getChestLocations() {
    return chestLocations;
  }
}
