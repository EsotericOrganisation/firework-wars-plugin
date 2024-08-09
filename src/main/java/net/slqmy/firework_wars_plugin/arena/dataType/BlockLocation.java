package net.slqmy.firework_wars_plugin.arena.dataType;

import com.google.gson.annotations.Expose;

public class BlockLocation {

  @Expose
  private String worldName;

  @Expose
  private int x;
  @Expose
  private int y;
  @Expose
  private int z;

  public String getWorldName() {
    return worldName;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }
}
