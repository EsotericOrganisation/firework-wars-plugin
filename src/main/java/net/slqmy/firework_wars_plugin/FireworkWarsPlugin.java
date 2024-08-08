package net.slqmy.firework_wars_plugin;

import net.slqmy.firework_wars_plugin.util.PersistentDataManager;
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
import net.slqmy.firework_wars_plugin.items.manager.CustomItemManager;
import net.slqmy.firework_wars_plugin.language.LanguageManager;

@DefaultQualifier(NonNull.class)
public final class FireworkWarsPlugin extends JavaPlugin {

  private PlayerDataManager playerDataManager;
  private LanguageManager languageManager;
  private ArenaManager arenaManager;
  private GameManager gameManager;
  private CustomItemManager customItemManager;
  private PersistentDataManager pdcManager;

  public PlayerDataManager getPlayerDataManager() {
    return this.playerDataManager;
  }

  public LanguageManager getLanguageManager() {
    return this.languageManager;
  }

  public ArenaManager getArenaManager() {
    return this.arenaManager;
  }

  public GameManager getGameManager() {
    return this.gameManager;
  }

  public CustomItemManager getCustomItemManager() {
    return this.customItemManager;
  }

  public PersistentDataManager getPdcManager() {
    return this.pdcManager;
  }

  @Override
  public void onEnable() {
    getDataFolder().mkdir();
    saveDefaultConfig();

    CommandAPIBukkitConfig commandAPIConfig = new CommandAPIBukkitConfig(this);

    CommandAPI.onLoad(commandAPIConfig);
    CommandAPI.onEnable();

    this.playerDataManager = new PlayerDataManager(this);
    this.languageManager = new LanguageManager(this);
    this.arenaManager = new ArenaManager(this);
    this.gameManager = new GameManager();
    this.customItemManager = new CustomItemManager(this);
    this.pdcManager = new PersistentDataManager(this);

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
