package net.slqmy.firework_wars_plugin.items.manager;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.language.LanguageManager;
import net.slqmy.firework_wars_plugin.util.Keys;
import net.slqmy.firework_wars_plugin.util.PersistentDataManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

public abstract class AbstractItem implements Listener {

  protected final FireworkWarsPlugin plugin;
  protected final MiniMessage MM;

  protected final PersistentDataManager pdcManager;
  protected final LanguageManager languageManager;

  protected final String itemId;
  protected final Material itemMaterial;

  protected final NamespacedKey isItemKey;

  public AbstractItem(FireworkWarsPlugin plugin, String itemId, Material itemMaterial) {
    this.plugin = plugin;
    this.MM = MiniMessage.miniMessage();

    this.pdcManager = plugin.getPdcManager();
    this.languageManager = plugin.getLanguageManager();

    this.itemId = itemId;
    this.itemMaterial = itemMaterial;

    this.isItemKey = Keys.CUSTOM_ITEM_ID;

    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public String getItemId() {
    return itemId;
  }

  @Contract("null -> false")
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

  public abstract ItemStack getItem(Player player);
}
