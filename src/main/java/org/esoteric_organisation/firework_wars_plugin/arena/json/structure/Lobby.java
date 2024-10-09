package org.esoteric_organisation.firework_wars_plugin.arena.json.structure;

import org.esoteric_organisation.firework_wars_plugin.arena.json.mini_components.PlayerLocation;

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
