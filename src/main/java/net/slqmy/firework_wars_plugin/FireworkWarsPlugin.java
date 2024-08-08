package net.slqmy.firework_wars_plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.slqmy.firework_wars_plugin.arena.ArenaManager;
import net.slqmy.firework_wars_plugin.commands.ArenaCommand;
import net.slqmy.firework_wars_plugin.commands.SetLanguageCommand;
import net.slqmy.firework_wars_plugin.data.player.PlayerDataManager;
import net.slqmy.firework_wars_plugin.game.GameManager;
import net.slqmy.firework_wars_plugin.items.CustomItemManager;
import net.slqmy.firework_wars_plugin.language.LanguageManager;

@DefaultQualifier(NonNull.class)
public final class FireworkWarsPlugin extends JavaPlugin {

  private PlayerDataManager playerDataManager;
  private LanguageManager languageManager;
  private ArenaManager arenaManager;
  private GameManager gameManager;
  private CustomItemManager customItemManager;

  public PlayerDataManager getPlayerDataManager() {
    return playerDataManager;
  }

  public LanguageManager getLanguageManager() {
    return languageManager;
  }

  public ArenaManager getArenaManager() {
    return arenaManager;
  }

  public GameManager getGameManager() {
    return gameManager;
  }

  public CustomItemManager getCustomItemManager() {
    return customItemManager;
  }

  @Override
  public void onEnable() {
    getDataFolder().mkdir();
    saveDefaultConfig();

    CommandAPIBukkitConfig commandAPIConfig = new CommandAPIBukkitConfig(this);

    CommandAPI.onLoad(commandAPIConfig);
    CommandAPI.onEnable();

    playerDataManager = new PlayerDataManager(this);
    languageManager = new LanguageManager(this);
    arenaManager = new ArenaManager(this);
    gameManager = new GameManager(this);
    customItemManager = new CustomItemManager(this);

    new SetLanguageCommand(this);

    new ArenaCommand(this);
  }

  @Override
  public void onDisable() {
    if (playerDataManager != null) {
      playerDataManager.save();
    }
  }
}
