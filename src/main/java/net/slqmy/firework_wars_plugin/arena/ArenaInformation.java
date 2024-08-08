package net.slqmy.firework_wars_plugin.arena;

import com.google.gson.annotations.Expose;

import net.slqmy.firework_wars_plugin.arena.Arena;

public class ArenaInformation {

  @Expose
  private Arena[] arenas;

  public Arena[] getArenas() {
    return arenas;
  }
}
