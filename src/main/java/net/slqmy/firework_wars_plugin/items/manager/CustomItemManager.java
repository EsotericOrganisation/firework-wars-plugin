package net.slqmy.firework_wars_plugin.items.manager;

import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.items.FireworkRifleItem;
import net.slqmy.firework_wars_plugin.items.RifleAmmo;
import net.slqmy.firework_wars_plugin.items.nms.CustomCrossbow;
import net.slqmy.firework_wars_plugin.util.ReflectUtil;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Ref;
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

    reopenItemRegistry(); //Prevent error

    registerNMSItem(
        "crossbow",
        new CustomCrossbow(CustomCrossbow.PROPERTIES, getItem("firework_rifle_ammo")),
        Items.CROSSBOW);

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

  public ItemStack getBukkitItemStackFromNMS(String itemId) {
    net.minecraft.world.item.ItemStack nmsItemStack = nmsItemRegistry.get(itemId).getDefaultInstance();
    ItemStack itemStack = nmsItemStack.asBukkitCopy();

    itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItemStack));
    return itemStack;
  }

  private void registerItem(AbstractItem item) {
    itemRegistry.put(item.getItemId(), item);
  }

  private void registerNMSItem(String id, Item item, Item override) {
    overrideItemRegistryEntry(id, item, override);

    nmsItemRegistry.put(id, item);
  }

  private void reopenItemRegistry() {
    ReflectUtil.reflect(MappedRegistry.class, BuiltInRegistries.ITEM, () -> {
        ReflectUtil.setFieldValue("frozen", false);
        ReflectUtil.setFieldValue("unregisteredIntrusiveHolders", new IdentityHashMap<>());
    });
  }

  private void overrideItemRegistryEntry(String id, Item item, Item override) {
    ResourceKey<Item> key = ResourceKey.create(
        BuiltInRegistries.ITEM.key(), ResourceLocation.withDefaultNamespace(id));

    RegistrationInfo info = RegistrationInfo.BUILT_IN;

    Holder.Reference<Item> holder = ReflectUtil.reflect(MappedRegistry.class, BuiltInRegistries.ITEM, () -> {
      Map<Item, Holder.Reference<Item>> map = ReflectUtil.getFieldValue("unregisteredIntrusiveHolders");
      return map.get(item);
    });

    ReflectUtil.reflect(Holder.Reference.class, holder, () -> {
      ReflectUtil.invokeMethod("bindKey", new Class<?>[] {ResourceKey.class}, key);
    });

    ReflectUtil.reflect(MappedRegistry.class, BuiltInRegistries.ITEM, () -> {
      HashMap<ResourceKey<Item>, Holder.Reference<Item>> byKey = ReflectUtil.getFieldValue("byKey");
      byKey.put(key, holder);

      HashMap<ResourceLocation, Holder.Reference<Item>> byLocation = ReflectUtil.getFieldValue("byLocation");
      byLocation.put(key.location(), holder);

      IdentityHashMap<Item, Holder.Reference<Item>> byValue = ReflectUtil.getFieldValue("byValue");
      byValue.put(item, holder);

      ObjectArrayList<Holder.Reference<Item>> byId = ReflectUtil.getFieldValue("byId");
      byId.set(byId.indexOf(byValue.remove(override)), holder);

      Reference2IntOpenHashMap<Item> toId = ReflectUtil.getFieldValue("toId");
      toId.put(item, toId.getInt(override));
      toId.removeInt(override);

      IdentityHashMap<ResourceKey<Item>, RegistrationInfo> registrationInfos = ReflectUtil.getFieldValue("registrationInfos");
      registrationInfos.put(key, info);

      Lifecycle lifecycle = ReflectUtil.getFieldValue("registryLifecycle");
      ReflectUtil.setFieldValue("registryLifecycle", lifecycle.add(info.lifecycle()));

      Map<Item, Holder.Reference<Item>> unregisteredHolders = ReflectUtil.getFieldValue("unregisteredIntrusiveHolders");
      unregisteredHolders.remove(item);
    });
  }
}
