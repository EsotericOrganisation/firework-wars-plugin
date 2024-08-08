package net.slqmy.firework_wars_plugin.items.manager;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.items.FireworkRifleItem;
import net.slqmy.firework_wars_plugin.items.RifleAmmo;

import java.util.HashMap;
import java.util.Map;

public class CustomItemManager {
  private final Map<String, AbstractItem> itemRegistry = new HashMap<>();

  public CustomItemManager(FireworkWarsPlugin plugin) {
    this.registerItem(new FireworkRifleItem(plugin));
    this.registerItem(new RifleAmmo(plugin));
  }

  public Map<String, AbstractItem> getItemRegistry() {
    return this.itemRegistry;
  }

  public AbstractItem getItem(String itemId) {
    return this.itemRegistry.get(itemId);
  }

  private void registerItem(AbstractItem item) {
    itemRegistry.put(item.getItemId(), item);
  }
}
