package org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder;

import org.bukkit.Color;
import org.esoteric_organisation.firework_wars_plugin.arena.json.mini_components.PlayerLocation;
import org.esoteric_organisation.firework_wars_plugin.arena.json.mini_components.TeamColor;

@SuppressWarnings("unused")
public class TeamData {
  private String miniMessageString;
  private TeamColor color;
  private PlayerLocation spawnLocation;

  public String getMiniMessageString() {
    return miniMessageString;
  }

  public Color getColor() {
    return color.toBukkit();
  }

  public PlayerLocation getSpawnLocation() {
    return spawnLocation;
  }
}
