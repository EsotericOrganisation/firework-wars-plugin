package net.slqmy.firework_wars_plugin.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;

public abstract class AbstractItem implements Listener {

  protected final FireworkWarsPlugin plugin;

  protected final String itemId;
  protected final Material itemMaterial;

  protected final NamespacedKey isItemKey;

  public AbstractItem(FireworkWarsPlugin plugin, String itemId, Material itemMaterial) {
    this.plugin = plugin;
    this.itemId = itemId;
    this.itemMaterial = itemMaterial;

    isItemKey = new NamespacedKey(plugin, "custom_item_id");

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  public boolean isItem(ItemStack itemStack) {
    if (itemStack == null) {
      return false;
    }

    if (!itemStack.hasItemMeta()) {
      return false;
    }

    PersistentDataContainer dataContainer = itemStack.getItemMeta().getPersistentDataContainer();
    String itemStackItemId = dataContainer.get(isItemKey, PersistentDataType.STRING);
    return itemId.equals(itemStackItemId);
  }

  protected ItemStack getBaseItemStack() {
    return new ItemStack(itemMaterial);
  }

  protected abstract ItemStack getItem(Player player);
}
