package net.slqmy.firework_wars_plugin.arena;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;

public class ArenaManager {

  private final FireworkWarsPlugin plugin;

  private ArenaInformation arenaInformation = null;

  private final String arenasResourcePath = "arenas.json";
  private final String arenasFilePath;

  public ArenaInformation getArenaInformation() {
    return arenaInformation;
  }

  public ArenaManager(FireworkWarsPlugin plugin) {
    this.plugin = plugin;

    plugin.saveResource(arenasResourcePath, false);
    arenasFilePath = plugin.getDataFolder().getPath() + File.separator + arenasResourcePath;
    File file = new File(arenasFilePath);

    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    try (Reader reader = new FileReader(file)) {
      arenaInformation = gson.fromJson(reader, ArenaInformation.class);
      reader.close();
    } catch (IOException exception) {
      exception.printStackTrace();
      return;
    }
  }
}
