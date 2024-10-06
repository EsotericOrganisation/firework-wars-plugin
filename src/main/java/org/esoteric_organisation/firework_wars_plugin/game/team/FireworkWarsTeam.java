package org.esoteric_organisation.firework_wars_plugin.game.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.structure.ConfiguredTeam;
import org.esoteric_organisation.firework_wars_plugin.language.Message;

import java.util.ArrayList;
import java.util.List;

public class FireworkWarsTeam {

  private final FireworkWarsPlugin plugin;

  private final ConfiguredTeam configuredTeam;

  private final List<TeamPlayer> players = new ArrayList<>();

  public ConfiguredTeam getConfiguredTeam() {
    return configuredTeam;
  }

  public List<TeamPlayer> getPlayers() {
    return players;
  }

  public FireworkWarsTeam(ConfiguredTeam configuredTeam, FireworkWarsPlugin plugin) {
    this.configuredTeam = configuredTeam;
    this.plugin = plugin;
  }

  public void addPlayer(TeamPlayer teamPlayer) {
    players.add(teamPlayer);
    teamPlayer.setTeam(this);

    Player player = teamPlayer.getPlayer();
    player.teleport(configuredTeam.getSpawnLocation().getBukkitLocation());

    player.sendTitlePart(TitlePart.TITLE, plugin.getLanguageManager().getMessage(Message.YOU_ARE_ON_TEAM, player));
    player.sendTitlePart(TitlePart.SUBTITLE, getColoredTeamName());
  }

  public Component getColoredTeamName() {
    return configuredTeam.getDeserializedTeamName();
  }

  public TextColor getTeamColor() {
    return getColoredTeamName().color();
  }
}
