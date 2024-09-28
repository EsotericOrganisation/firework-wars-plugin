package org.esoteric_organisation.firework_wars_plugin.arena.data_type;

import com.google.gson.annotations.Expose;
import org.bukkit.Color;
import org.bukkit.DyeColor;

public class TeamColor {
  @Expose
  private String color;

  public String getColor() {
    return color;
  }

  public Color toBukkit() {
    return DyeColor.valueOf(color.toUpperCase()).getColor();
  }
}
