package net.slqmy.firework_wars_plugin.items;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
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
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.format.TextDecoration;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.items.manager.AbstractItem;
import net.slqmy.firework_wars_plugin.language.Message;

import java.util.List;

public class FireworkRifleItem extends AbstractItem {

  public FireworkRifleItem(FireworkWarsPlugin plugin) {
    super(plugin, "firework_rifle", Material.CROSSBOW);
  }

  @Override
  protected ItemStack getItem(Player player) {
    AbstractItem ammoItem = plugin.getCustomItemManager().getItem("firework_rifle_ammo");

    return new ItemBuilder<CrossbowMeta>(plugin, itemMaterial)
      .setName(plugin.getLanguageManager().getMessage(Message.FIREWORK_RIFLE, player))
      .setLore(plugin.getLanguageManager().getMessage(Message.FIREWORK_RIFLE_LORE, player))
      .setEnchanted(true)
      .setUnbreakable(true)
      .itemSupplier(() -> new CustomCrossbow(ammoItem).getDefaultInstance().asBukkitCopy())
      .modifyMeta(meta -> meta.addEnchant(Enchantment.QUICK_CHARGE, 3, true))
      .build();
  }

  @EventHandler
  public void onCrossbowLoad(EntityLoadCrossbowEvent event) {
    CrossbowMeta meta = (CrossbowMeta) event.getCrossbow().getItemMeta();

    ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
    FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();
  }

  @EventHandler
  public void onCrossbowShoot(EntityShootBowEvent event) {

  }

  private void addFireworkStars(FireworkMeta meta, Color color, int count) {
    for (int i = 0; i < count; i++) {
      meta.addEffect(FireworkEffect
          .builder()
          .withColor(Color.WHITE)
          .withFade(color)
          .withFlicker()
          .build());
    }
  }
}
