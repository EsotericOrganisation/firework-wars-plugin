package net.slqmy.firework_wars_plugin.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.format.TextDecoration;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.items.AbstractItem;
import net.slqmy.firework_wars_plugin.language.Message;

public class FireworkRifleItem extends AbstractItem {

  public FireworkRifleItem(FireworkWarsPlugin plugin) {
    super(plugin, "firework_rifle", Material.CROSSBOW);
  }

  @Override
  protected ItemStack getItem(Player player) {
    ItemStack item = getBaseItemStack();
    ItemMeta meta = item.getItemMeta();

    meta.displayName(plugin.getLanguageManager().getMessage(Message.FIREWORK_RIFLE, player).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));

    item.setItemMeta(meta);

    return item;
  }
}
