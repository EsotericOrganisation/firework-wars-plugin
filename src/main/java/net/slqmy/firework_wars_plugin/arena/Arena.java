package net.slqmy.firework_wars_plugin.arena;

import org.bukkit.Location;

import com.google.gson.annotations.Expose;

import net.slqmy.firework_wars_plugin.arena.BlockLocation;
import net.slqmy.firework_wars_plugin.arena.PlayerLocation;

public class Arena {

  @Expose
  private PlayerLocation lobbySpawnLocation;
  @Expose
  private BlockLocation[] chestLocations;

  public PlayerLocation getLobbySpawnLocation() {
    return lobbySpawnLocation;
  }

  public BlockLocation[] getChestLocations() {
    return chestLocations;
  }
}
