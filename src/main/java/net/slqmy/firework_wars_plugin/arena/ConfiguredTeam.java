package net.slqmy.firework_wars_plugin.arena;

import com.google.gson.annotations.Expose;

public class ConfiguredTeam {

  @Expose
  private String miniMessageString;
  @Expose
  private PlayerLocation spawnLocation;

  public String getMiniMessageString() {
    return miniMessageString;
  }

  public PlayerLocation getSpawnLocation() {
    return spawnLocation;
  }
}
