package net.slqmy.firework_wars_plugin.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.gson.annotations.Expose;

public class PlayerLocation {

  @Expose
  private String worldName;

  @Expose
  private float x;
  @Expose
  private float y;
  @Expose
  private float z;

  @Expose
  private float pitch;
  @Expose
  private float yaw;

  public String getWorldName() {
    return worldName;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float getZ() {
    return z;
  }

  public float getPitch() {
    return pitch;
  }

  public float getYaw() {
    return yaw;
  }

  public Location getBukkitLocation() {
    return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
  }
}
