package org.esoteric_organisation.firework_wars_plugin.game;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.structure.ConfiguredTeam;
import org.esoteric_organisation.firework_wars_plugin.language.Message;

public class FireworkWarsTeam {

  private final FireworkWarsPlugin plugin;

  private final ConfiguredTeam configuredTeam;

  private final List<Player> players = new ArrayList<>();

  public ConfiguredTeam getConfiguredTeam() {
    return configuredTeam;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public FireworkWarsTeam(ConfiguredTeam configuredTeam, FireworkWarsPlugin plugin) {
    this.configuredTeam = configuredTeam;
    this.plugin = plugin;
  }

  public void addPlayer(Player player) {
    players.add(player);
    player.teleport(configuredTeam.getSpawnLocation().getBukkitLocation());

    player.sendTitlePart(TitlePart.TITLE, plugin.getLanguageManager().getMessage(Message.YOU_ARE_ON_TEAM, player));
    player.sendTitlePart(TitlePart.SUBTITLE, getDeserializedTeamName());
  }

  public Component getDeserializedTeamName() {
    return configuredTeam.getDeserializedTeamName();
  }

  public TextColor getTeamColor() {
    return getDeserializedTeamName().color();
  }
}
