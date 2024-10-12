package org.esoteric.minecraft.plugins.fireworkwars.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerVelocityManager {

    private final Map<UUID, Vector> playerVelocityMap = new HashMap<>();
    private final Map<UUID, Vector> playerPreviousPositionMap = new HashMap<>();

    public PlayerVelocityManager(@NotNull FireworkWarsPlugin plugin) {
        plugin.runTaskTimer(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Vector previousPosition = playerPreviousPositionMap.get(player.getUniqueId());

                if (previousPosition == null) {
                    playerPreviousPositionMap.put(player.getUniqueId(), player.getLocation().toVector());
                    continue;
                }

                Vector velocity = player.getLocation().toVector().subtract(previousPosition);
                playerVelocityMap.put(player.getUniqueId(), velocity);
            }
        }, 0L, 1L);
    }

    public Vector getPlayerVelocity(@NotNull Player player) {
        return playerVelocityMap.get(player.getUniqueId());
    }
}
