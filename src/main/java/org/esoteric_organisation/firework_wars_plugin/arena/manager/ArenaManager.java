package org.esoteric_organisation.firework_wars_plugin.arena.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.structure.ArenaInformation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class ArenaManager {
  private static final String ARENAS_RESOURCE_PATH = "arenas.json";

  private final FireworkWarsPlugin plugin;
  private ArenaInformation arenaInformation;

  public ArenaInformation getArenaInformation() {
    return arenaInformation;
  }

  public ArenaManager(FireworkWarsPlugin plugin) {
    this.plugin = plugin;

    loadArenasFromConfig();
  }

  private void loadArenasFromConfig () {
    plugin.saveResource(ARENAS_RESOURCE_PATH, false);

    String arenasFilePath = plugin.getDataFolder().getPath() + File.separator + ARENAS_RESOURCE_PATH;
    File file = new File(arenasFilePath);

    Gson gson = new GsonBuilder()
      .excludeFieldsWithoutExposeAnnotation()
      .create();

    try (Reader reader = new FileReader(file)) {
      arenaInformation = gson.fromJson(reader, ArenaInformation.class);
    } catch (IOException exception) {
      plugin.getLogger().severe("Failed to load arenas.json file: " + exception.getMessage());
    }
  }
}
