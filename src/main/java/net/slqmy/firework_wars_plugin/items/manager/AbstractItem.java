package net.slqmy.firework_wars_plugin.items.manager;

import net.kyori.adventure.text.minimessage.MiniMessage;
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
  protected final MiniMessage MM;

  protected final String itemId;
  protected final Material itemMaterial;

  protected final NamespacedKey isItemKey;

  public AbstractItem(FireworkWarsPlugin plugin, String itemId, Material itemMaterial) {
    this.plugin = plugin;
    this.MM = MiniMessage.miniMessage();

    this.itemId = itemId;
    this.itemMaterial = itemMaterial;

    this.isItemKey = new NamespacedKey(plugin, "custom_item_id");

    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public String getItemId() {
    return itemId;
  }

  public boolean isValidCustomItem(ItemStack itemStack) {
    if (itemStack == null) {
      return false;
    }

    if (!itemStack.hasItemMeta()) {
      return false;
    }

    String itemStackItemId = plugin.getPdcManager().getStringValue(itemStack.getItemMeta(), isItemKey);
    return itemId.equals(itemStackItemId);
  }

  protected ItemStack getBaseItemStack() {
    return new ItemStack(itemMaterial);
  }

  protected abstract ItemStack getItem(Player player);
}
