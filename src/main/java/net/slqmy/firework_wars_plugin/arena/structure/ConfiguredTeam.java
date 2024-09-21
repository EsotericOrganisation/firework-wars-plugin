package net.slqmy.firework_wars_plugin.arena.structure;

import com.google.gson.annotations.Expose;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.firework_wars_plugin.arena.data_type.PlayerLocation;

import net.slqmy.firework_wars_plugin.arena.data_type.TeamColor;
import org.bukkit.Color;

public class ConfiguredTeam {

  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  @Expose
  private String miniMessageString;
  @Expose
  private TeamColor color;
  @Expose
  private PlayerLocation spawnLocation;

  public String getMiniMessageString() {
    return miniMessageString;
  }

  public Color getColor() {
    return color.toBukkit();
  }

  public PlayerLocation getSpawnLocation() {
    return spawnLocation;
  }

  public Component getDeserializedTeamName() {
    return miniMessage.deserialize(miniMessageString);
  }
}
