package net.slqmy.firework_wars_plugin;

import net.slqmy.firework_wars_plugin.util.PersistentDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.slqmy.firework_wars_plugin.arena.manager.ArenaManager;
import net.slqmy.firework_wars_plugin.commands.ArenaCommand;
import net.slqmy.firework_wars_plugin.commands.SetLanguageCommand;
import net.slqmy.firework_wars_plugin.data.player.PlayerDataManager;
import net.slqmy.firework_wars_plugin.game.GameManager;
import net.slqmy.firework_wars_plugin.items.manager.CustomItemManager;
import net.slqmy.firework_wars_plugin.language.LanguageManager;

import java.util.logging.Logger;

public final class FireworkWarsPlugin extends JavaPlugin implements Listener {
  public static Logger LOGGER;

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

  public FireworkWarsPlugin() {
    LOGGER = getLogger();
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
    this.gameManager = new GameManager(this);
    this.customItemManager = new CustomItemManager(this);
    this.pdcManager = new PersistentDataManager(this);

    new SetLanguageCommand(this);
    new ArenaCommand(this);

    getServer().getPluginManager().registerEvents(this, this);
  }

  @Override
  public void onDisable() {
    if (playerDataManager != null) {
      playerDataManager.save();
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    ItemStack item1 = customItemManager.getItem("firework_rifle_ammo").getItem(event.getPlayer());
    ItemStack item2 = customItemManager.getItem("firework_rifle").getItem(event.getPlayer());

    event.getPlayer().getInventory().addItem(item1, item2);
  }
}
