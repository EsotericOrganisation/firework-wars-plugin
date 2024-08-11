package net.slqmy.firework_wars_plugin.items;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import net.slqmy.firework_wars_plugin.game.FireworkWarsGame;
import net.slqmy.firework_wars_plugin.game.FireworkWarsTeam;
import net.slqmy.firework_wars_plugin.items.nms.CustomCrossbow;
import net.slqmy.firework_wars_plugin.util.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.FireworkMeta;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.items.manager.AbstractItem;
import net.slqmy.firework_wars_plugin.language.Message;

import java.util.List;

public class FireworkRifleItem extends AbstractItem {

  public FireworkRifleItem(FireworkWarsPlugin plugin) {
    super(plugin, "firework_rifle", Material.CROSSBOW);
  }

  @Override
  public ItemStack getItem(Player player) {
    return new ItemBuilder<CrossbowMeta>(plugin, itemMaterial)
      .setName(plugin.getLanguageManager().getMessage(Message.FIREWORK_RIFLE, player))
      .setLore(plugin.getLanguageManager().getMessage(Message.FIREWORK_RIFLE_LORE, player))
      .setEnchanted(true)
      .setUnbreakable(true)
      .itemSupplier(() -> plugin.getCustomItemManager().getBukkitItemStackFromNMS("crossbow"))
      .modifyMeta(meta -> meta.addEnchant(Enchantment.QUICK_CHARGE, 3, true))
      .build();
  }

  @EventHandler
  public void onCrossbowLoad(EntityLoadCrossbowEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    FireworkWarsGame game = plugin.getGameManager().getFireworkWarsGame(player);

    if (game == null) {
      return;
    }

    FireworkWarsTeam team = game.getTeam(player);

    ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
    FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();

    addFireworkStars(fireworkMeta, team.getConfiguredTeam().getColor());
    firework.setItemMeta(fireworkMeta);

    CrossbowMeta crossbowMeta = (CrossbowMeta) event.getCrossbow().getItemMeta();
    crossbowMeta.setChargedProjectiles(List.of(firework));
  }

  @EventHandler
  public void onCrossbowShoot(EntityShootBowEvent event) {

  }

  private void addFireworkStars(FireworkMeta meta, Color color) {
    for (int i = 0; i < 4; i++) {
      meta.addEffect(FireworkEffect
          .builder()
          .withColor(Color.WHITE)
          .withFade(color)
          .withFlicker()
          .build());
    }
  }
}
