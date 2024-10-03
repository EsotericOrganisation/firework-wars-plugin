package org.esoteric_organisation.firework_wars_plugin.items.guns.rocket_launcher;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame;
import org.esoteric_organisation.firework_wars_plugin.items.guns.BaseGunItem;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.esoteric_organisation.firework_wars_plugin.util.Keys;
import org.esoteric_organisation.firework_wars_plugin.util.PersistentDataManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RocketLauncherItem extends BaseGunItem {

  public RocketLauncherItem(FireworkWarsPlugin plugin) {
    super(plugin, "rocket_launcher", "rocket_launcher_ammo");
  }

  @Override
  protected void onCrossbowLoad(Player player, FireworkWarsGame game, EntityLoadCrossbowEvent event) {

  }

  @Override
  protected void onCrossbowShoot(Player player, FireworkWarsGame game, EntityShootBowEvent event) {
    PersistentDataContainer pdc = event.getProjectile().getPersistentDataContainer();

    pdc.set(Keys.EXPLOSIVE_ROCKET_ENTITY, PersistentDataType.BOOLEAN, true);
  }

  @Override
  public ItemStack getItem(Player player) {
    ItemStack rocketLauncher = new ItemStack(Material.CROSSBOW);

    rocketLauncher.editMeta(
      (meta) -> {
        meta.itemName(
          plugin.getLanguageManager().getMessage(Message.ROCKET_LAUNCHER, player)
        );

        meta.lore(List.of(plugin.getLanguageManager().getMessage(Message.ROCKET_LAUNCHER_LORE, player)));
      }
    );

    return rocketLauncher;
  }

  @EventHandler
  public void onFireworkExplode(@NotNull FireworkExplodeEvent event) {
    Firework firework = event.getEntity();

    Boolean isRocket = firework.getPersistentDataContainer().get(Keys.EXPLOSIVE_ROCKET_ENTITY, PersistentDataType.BOOLEAN);

    if (isRocket != null && isRocket) {
      event.setCancelled(true);

      firework.getWorld().createExplosion(firework, firework.getLocation(), 3.5F, false, true);

      firework.remove();
    }
  }
}
