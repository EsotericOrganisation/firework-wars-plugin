package net.slqmy.firework_wars_plugin.arena;

import com.google.gson.annotations.Expose;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ConfiguredTeam {

  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  @Expose
  private String miniMessageString;
  @Expose
  private PlayerLocation spawnLocation;

  public String getMiniMessageString() {
    return miniMessageString;
  }

  public PlayerLocation getSpawnLocation() {
    return spawnLocation;
  }

  public Component getDeserializedTeamName() {
    return miniMessage.deserialize(miniMessageString);
  }
}
