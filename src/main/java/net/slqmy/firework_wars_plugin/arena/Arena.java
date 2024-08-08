package net.slqmy.firework_wars_plugin.arena;

import org.bukkit.Location;

import com.google.gson.annotations.Expose;

import net.slqmy.firework_wars_plugin.arena.BlockLocation;

public class Arena {

  @Expose
  private Location lobbySpawnLocation;
  @Expose
  private BlockLocation[] blockLocations;

  public Location getLobbySpawnLocation() {
    return lobbySpawnLocation;
  }

  public BlockLocation[] getBlockLocations() {
    return blockLocations;
  }
}
