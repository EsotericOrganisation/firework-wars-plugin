package org.esoteric_organisation.firework_wars_plugin.arena.manager;

import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder.Arena;
import org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder.Lobby;
import org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder.TeamData;
import org.esoteric_organisation.firework_wars_plugin.arena.json.mini_components.BlockLocation;
import org.esoteric_organisation.firework_wars_plugin.arena.json.mini_components.PlayerLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.stream;

public class ConfigValidator {
    private final FireworkWarsPlugin plugin;
    private final ArenaManager arenaManager;

    private final List<String> lobbyWorlds = new ArrayList<>();
    private final List<String> arenaWorlds = new ArrayList<>();

    public ConfigValidator(FireworkWarsPlugin plugin, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
    }

    public void validate() {
        List<Lobby> lobbies = arenaManager.getLobbies();
        List<Arena> arenas = arenaManager.getArenas();

        if (lobbies == null || lobbies.isEmpty()) {
            plugin.getLogger().warning("No lobbies found in arenas.json file; Impossible to start games.");
        } else {
            validateLobbies();
        }

        if (arenas == null || arenas.isEmpty()) {
            plugin.getLogger().warning("No arenas found in arenas.json file.");
        } else {
            validateArenas();
        }

        lobbyWorlds.clear();
        arenaWorlds.clear();
    }

    private void validateLobbies() {
        for (Lobby lobby : arenaManager.getLobbies()) {
            String worldName = lobby.getWorld();

            validateString(worldName, "Invalid lobby configuration: No world set.");

            if (lobbyWorlds.contains(worldName)) {
                throw new IllegalArgumentException("Invalid lobby configuration: Duplicate world names.");
            } else {
                lobbyWorlds.add(worldName);
            }

            if (plugin.getServer().getWorld(worldName) == null) {
                throw new IllegalArgumentException("Invalid lobby configuration: World " + worldName + " does not exist.");
            }

            validatePlayerLocation(lobby.getSpawnLocation(), worldName);
        }
    }

    private void validateArenas() {
        for (Arena arena : arenaManager.getArenas()) {
            List<String> worldNames = arena.getWorlds();

            if (worldNames == null || worldNames.isEmpty()) {
                throw new IllegalArgumentException("Invalid arena configuration: No worlds set.");
            }

            Predicate<String> isAlreadyUsedInArena = arenaWorlds::contains;
            Predicate<String> isAlreadyUsedInLobby = lobbyWorlds::contains;

            if (worldNames.stream().anyMatch(isAlreadyUsedInLobby.and(isAlreadyUsedInArena))) {
                throw new IllegalArgumentException("Invalid arena configuration: Duplicate world names.");
            } else {
                arenaWorlds.addAll(worldNames);
            }

            validatePlayerLocation(arena.getWaitingAreaLocation(), worldNames.toArray(String[]::new));

            for (TeamData teamData : arena.getTeamInformation()) {
                try {
                    teamData.getColor();
                } catch (IllegalArgumentException exception) {
                    throw new IllegalArgumentException("Invalid team configuration: Invalid color.");
                }

                validatePlayerLocation(teamData.getSpawnLocation(), worldNames.toArray(String[]::new));
                validateString(teamData.getMiniMessageString(), "Invalid team configuration: No team name set.");
            }


            for (BlockLocation chestLocation : arena.getChestLocations()) {
                validateBlockLocation(chestLocation, worldNames.toArray(String[]::new));
            }
        }
    }

    private void validatePlayerLocation(PlayerLocation playerLocation, String... worldNames) {
        if (playerLocation == null) {
            throw new IllegalArgumentException("Invalid player location: No player location set.");
        }

        validateString(playerLocation.getWorldName(), "Invalid player location: No world set.");

        if (stream(worldNames).noneMatch(name -> name.equals(playerLocation.getWorldName()))) {
            throw new IllegalArgumentException("Invalid player location: Player location world does not match arena world.");
        }
    }

    private void validateBlockLocation(BlockLocation blockLocation, String... worldNames) {
        if (blockLocation == null) {
            throw new IllegalArgumentException("Invalid block location: No block location set.");
        }

        validateString(blockLocation.getWorldName(), "Invalid block location: No world set.");

        if (stream(worldNames).noneMatch(name -> name.equals(blockLocation.getWorldName()))) {
            throw new IllegalArgumentException("Invalid block location: Block location world does not match arena world.");
        }
    }

    private void validateString(String string, String message) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
