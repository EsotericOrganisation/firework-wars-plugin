package net.slqmy.firework_wars_plugin.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.slqmy.firework_wars_plugin.arena.ConfiguredTeam;

public class FireworkWarsTeam {

  private final ConfiguredTeam configuredTeam;

  private final List<Player> players = new ArrayList<>();

  public ConfiguredTeam getConfiguredTeam() {
    return configuredTeam;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public FireworkWarsTeam(ConfiguredTeam configuredTeam) {
    this.configuredTeam = configuredTeam;
  }

  public void addPlayer(Player player) {
    players.add(player);
    player.teleport(configuredTeam.getSpawnLocation().getBukkitLocation());
  }
}
