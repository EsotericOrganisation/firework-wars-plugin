package net.slqmy.firework_wars_plugin.event.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.game.FireworkWarsGame;

public class GameEventListener implements Listener {
  
  private final FireworkWarsPlugin plugin;

  public GameEventListener(FireworkWarsPlugin plugin) {
    this.plugin = plugin;

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getPlayer();
    FireworkWarsGame game = plugin.getGameManager().getFireworkWarsGame(player);

    if (game == null) {
      return;
    }

    game.onPlayerDeath(event);
  }
}
