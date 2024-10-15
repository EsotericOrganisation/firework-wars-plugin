package org.esoteric.minecraft.plugins.fireworkwars.arena.json.data;

import org.bukkit.Location;
import org.bukkit.World;

@SuppressWarnings("unused")
public class WorldBorderData {
    private int centerX;
    private int centerZ;
    private int radius;

    public int getCenterX() {
        return centerX;
    }

    public int getCenterZ() {
        return centerZ;
    }

    public Location getCenter(World world) {
        return new Location(world, centerX, 0, centerZ);
    }

    public int getRadius() {
        return radius;
    }
}
