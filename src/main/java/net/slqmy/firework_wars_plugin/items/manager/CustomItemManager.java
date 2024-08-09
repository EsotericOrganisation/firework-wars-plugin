package net.slqmy.firework_wars_plugin.items.manager;

import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.items.FireworkRifleItem;
import net.slqmy.firework_wars_plugin.items.RifleAmmo;
import net.slqmy.firework_wars_plugin.items.nms.CustomCrossbow;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class CustomItemManager {
  private final FireworkWarsPlugin plugin;

  private final Map<String, AbstractItem> itemRegistry = new HashMap<>();
  private final Map<String, Item> nmsItemRegistry = new HashMap<>();

  public CustomItemManager(FireworkWarsPlugin plugin) {
    this.plugin = plugin;

    registerItem(new FireworkRifleItem(plugin));
    registerItem(new RifleAmmo(plugin));

    reopenItemRegistry();

    registerNMSItem(
        "firework_rifle_crossbow",
        new CustomCrossbow(CustomCrossbow.PROPERTIES, getItem("firework_rifle_ammo")));

    BuiltInRegistries.ITEM.freeze();
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

  private void reopenItemRegistry() {
    try {
      Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
      Field unregisteredHoldersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");

      frozenField.setAccessible(true);
      unregisteredHoldersField.setAccessible(true);

      frozenField.setBoolean(BuiltInRegistries.ITEM, false);
      unregisteredHoldersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
    } catch (IllegalAccessException | NoSuchFieldException e) {
      plugin.getLogger().severe("Failed to reopen the item registry! " + e.getMessage());
    }
  }
}
