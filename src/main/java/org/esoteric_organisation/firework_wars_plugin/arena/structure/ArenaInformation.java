package org.esoteric_organisation.firework_wars_plugin.arena.structure;

import com.google.gson.annotations.Expose;

public class ArenaInformation {

  @Expose
  private Arena[] arenas;

  public Arena[] getArenas() {
    return arenas;
  }
}
