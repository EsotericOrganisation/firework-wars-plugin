package net.slqmy.firework_wars_plugin.items.manager;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.items.FireworkRifleItem;
import net.slqmy.firework_wars_plugin.items.RifleAmmo;
import net.slqmy.firework_wars_plugin.items.nms.CustomCrossbow;

import java.util.HashMap;
import java.util.Map;

public class CustomItemManager {
  private final Map<String, AbstractItem> itemRegistry = new HashMap<>();
  private final Map<String, Item> nmsItemRegistry = new HashMap<>();

  public CustomItemManager(FireworkWarsPlugin plugin) {
    registerItem(new FireworkRifleItem(plugin));
    registerItem(new RifleAmmo(plugin));

    registerNMSItem(
        "firework_rifle_crossbow",
        new CustomCrossbow(CustomCrossbow.PROPERTIES, getItem("firework_rifle_ammo")));
  }

  public Map<String, AbstractItem> getItemRegistry() {
    return itemRegistry;
  }

  public Map<String, Item> getNMSItemRegistry() {
      return nmsItemRegistry;
  }

  public AbstractItem getItem(String itemId) {
    return itemRegistry.get(itemId);
  }

  public Item getNMSItem(String itemId) {
      return nmsItemRegistry.get(itemId);
  }

  private void registerItem(AbstractItem item) {
    itemRegistry.put(item.getItemId(), item);
  }

  private void registerNMSItem(String id, Item item) {
    Items.registerItem(id, item);
    nmsItemRegistry.put(id, item);
  }
}
