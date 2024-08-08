package net.slqmy.firework_wars_plugin.arena;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.Expose;

import net.slqmy.firework_wars_plugin.arena.BlockLocation;
import net.slqmy.firework_wars_plugin.arena.PlayerLocation;

public class Arena {

  @Expose
  private PlayerLocation lobbySpawnLocation;
  @Expose
  private @Nullable int minimumPlayerCount;
  @Expose
  private @Nullable int maximumPlayerCount;
  @Expose
  private PlayerLocation[] teamSpawnLocations;
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

  public PlayerLocation[] getTeamSpawnLocations() {
    return teamSpawnLocations;
  }

  public BlockLocation[] getChestLocations() {
    return chestLocations;
  }
}
