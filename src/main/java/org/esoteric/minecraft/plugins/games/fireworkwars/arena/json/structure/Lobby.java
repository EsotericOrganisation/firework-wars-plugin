package org.esoteric.minecraft.plugins.games.fireworkwars.arena.json.structure;

import org.esoteric.minecraft.plugins.games.fireworkwars.arena.json.components.PlayerLocation;

@SuppressWarnings("unused")
public class Lobby {
    private String world;
    private PlayerLocation spawnLocation;

    public String getWorld() {
        return world;
    }

    public PlayerLocation getSpawnLocation() {
        return spawnLocation;
    }
}
