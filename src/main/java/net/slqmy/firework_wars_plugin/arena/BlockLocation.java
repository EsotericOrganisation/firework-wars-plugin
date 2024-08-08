package net.slqmy.firework_wars_plugin.arena;

import com.google.gson.annotations.Expose;

public class BlockLocation {

  @Expose
  public String worldName;

  @Expose
  public int x;
  @Expose
  public int y;
  @Expose
  public int z;

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
