package org.esoteric_organisation.firework_wars_plugin.arena.json.structure;

import java.util.List;

@SuppressWarnings("unused")
public class ArenaInformation {
    private Lobby[] lobbies;
    private Arena[] arenas;

    public List<Lobby> getLobbies() {
        return lobbies == null ? null : List.of(lobbies);
    }

    public List<Arena> getArenas() {
        return arenas == null ? null : List.of(arenas);
    }
}
