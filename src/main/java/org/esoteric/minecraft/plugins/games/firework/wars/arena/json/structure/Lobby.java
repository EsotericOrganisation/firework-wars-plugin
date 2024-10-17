package org.esoteric.minecraft.plugins.games.firework.wars.arena.json.structure;

import org.esoteric.minecraft.plugins.games.firework.wars.arena.json.components.PlayerLocation;

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
