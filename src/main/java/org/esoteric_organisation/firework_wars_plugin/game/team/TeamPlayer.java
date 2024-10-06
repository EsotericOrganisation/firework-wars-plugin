package org.esoteric_organisation.firework_wars_plugin.game.team;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamPlayer {
    private final static Map<UUID, TeamPlayer> activePlayers = new HashMap<>();

    private final UUID uuid;
    private final FireworkWarsGame game;

    private FireworkWarsTeam team;

    public static TeamPlayer from(UUID uuid) {
        return activePlayers.get(uuid);
    }

    public static TeamPlayer from(Player player) {
        return from(player.getUniqueId());
    }

    public static void unregisterGame(FireworkWarsGame game) {
        game.getPlayers().forEach(player -> player.unregister(false));
    }

    public TeamPlayer(UUID uuid, FireworkWarsGame game) {
        this.uuid = uuid;
        this.game = game;

        register();
    }

    private void register() {
        activePlayers.forEach((uuid, player) -> {
            if (uuid.equals(this.uuid)) {
                player.unregister(true);
            }
        });

        activePlayers.put(uuid, this);
    }

    public void unregister(boolean removeFromGame) {
        activePlayers.remove(uuid);

        if (removeFromGame) {
          game.getPlayers().remove(this);
        }
    }

    public void setTeam(FireworkWarsTeam team) {
        this.team = team;
    }

    public FireworkWarsTeam getTeam() {
        return team;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getOfflinePlayer(uuid).getPlayer();
    }

    public Component getColoredName() {
        return getPlayer().displayName().color(team.getTeamColor());
    }

    public void sendMessage(Component message) {
        getPlayer().sendMessage(message);
    }
}
