package net.slqmy.firework_wars_plugin.arena;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;

public class ArenaManager {

  private final FireworkWarsPlugin plugin;

  private ArenaInformation arenaInformation = null;

  private final String arenasFilePath = "arenas.json";

  public ArenaManager(FireworkWarsPlugin plugin) {
    this.plugin = plugin;

    plugin.saveResource(arenasFilePath, false);
    File file = new File(arenasFilePath);

    Gson gson = new Gson();
    try (Reader reader = new FileReader(file)) {
      arenaInformation = gson.fromJson(reader, ArenaInformation.class);
    } catch (IOException exception) {
      exception.printStackTrace();
      return;
    }
  }
}
