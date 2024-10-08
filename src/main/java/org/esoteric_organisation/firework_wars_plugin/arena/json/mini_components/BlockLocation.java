package org.esoteric_organisation.firework_wars_plugin.arena.json.mini_components;

import org.bukkit.Bukkit;
import org.bukkit.Location;

@SuppressWarnings("unused")
public class BlockLocation {
    private String worldName;

    private int x;
    private int y;
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

    public Location getBukkitLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }
}
