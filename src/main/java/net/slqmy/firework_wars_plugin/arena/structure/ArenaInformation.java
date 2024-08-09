package net.slqmy.firework_wars_plugin.arena.structure;

import com.google.gson.annotations.Expose;

import net.slqmy.firework_wars_plugin.arena.structure.Arena;

public class ArenaInformation {

  @Expose
  private Arena[] arenas;

  public Arena[] getArenas() {
    return arenas;
  }
}
