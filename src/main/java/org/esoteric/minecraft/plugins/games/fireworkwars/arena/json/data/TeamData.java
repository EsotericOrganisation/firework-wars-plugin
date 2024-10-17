package org.esoteric.minecraft.plugins.games.fireworkwars.arena.json.data;

import org.bukkit.Color;
import org.esoteric.minecraft.plugins.games.fireworkwars.arena.json.components.PlayerLocation;
import org.esoteric.minecraft.plugins.games.fireworkwars.arena.json.components.TeamColor;

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

    public TeamColor getColorData() {
        return color;
    }

    public PlayerLocation getSpawnLocation() {
        return spawnLocation;
    }
}
