package org.esoteric.minecraft.plugins.fireworkwars.items;

import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.bukkit.inventory.meta.ItemMeta;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.items.armor.HeavyArmorItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.armor.MilitaryHelmetItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.consumables.GoldenAppleItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.consumables.HealingPotionItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.explosives.TNTItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.explosives.ThrowableTNTItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.guns.rifle.FireworkRifleItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.guns.rifle.RifleAmmo;
import org.esoteric.minecraft.plugins.fireworkwars.items.guns.rocket_launcher.RocketLauncherAmmo;
import org.esoteric.minecraft.plugins.fireworkwars.items.guns.rocket_launcher.RocketLauncherItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.guns.shotgun.FireworkShotgunItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.guns.shotgun.ShotgunAmmo;
import org.esoteric.minecraft.plugins.fireworkwars.items.misc.FlintAndSteelItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.misc.PlayerCompassItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.misc.ShearsItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.misc.WoolItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.nms.CustomCrossbow;
import org.esoteric.minecraft.plugins.fireworkwars.util.ReflectUtil;
import org.esoteric.minecraft.plugins.fireworkwars.util.Util;

import java.util.*;

@SuppressWarnings("unused")
public class CustomItemManager {
    private FireworkWarsPlugin plugin;
    private final ReflectUtil reflectUtil;

    private final Map<String, AbstractItem<? extends ItemMeta>> itemRegistry;
    private final Map<String, Item> nmsItemRegistry;

    public void setPlugin(FireworkWarsPlugin plugin) {
        this.plugin = plugin;
    }

    public CustomItemManager(ReflectUtil reflectUtil) {
        this.reflectUtil = reflectUtil;

        this.itemRegistry = new HashMap<>();
        this.nmsItemRegistry = new HashMap<>();
    }

    public void registerCustomItems() {
        registerItem(new FireworkRifleItem(plugin));
        registerItem(new RifleAmmo(plugin));

        registerItem(new FireworkShotgunItem(plugin));
        registerItem(new ShotgunAmmo(plugin));

        registerItem(new RocketLauncherItem(plugin));
        registerItem(new RocketLauncherAmmo(plugin));

        registerItem(new PlayerCompassItem(plugin));
        registerItem(new WoolItem(plugin));
        registerItem(new FlintAndSteelItem(plugin));
        registerItem(new ShearsItem(plugin));

        registerItem(new HealingPotionItem(plugin));
        registerItem(new GoldenAppleItem(plugin));

        registerItem(new TNTItem(plugin));
        registerItem(new ThrowableTNTItem(plugin));

        registerItem(new MilitaryHelmetItem(plugin));
        registerItem(new HeavyArmorItem(plugin));
    }

    public void registerNMSItems() {
        registerNMSItem(
                "crossbow",
                new CustomCrossbow(CustomCrossbow.PROPERTIES),
                Items.CROSSBOW);
    }

    public Map<String, AbstractItem<? extends ItemMeta>> getItemRegistry() {
        return itemRegistry;
    }

    public Map<String, Item> getNMSItemRegistry() {
        return nmsItemRegistry;
    }

    public AbstractItem<? extends ItemMeta> getItem(String itemId) {
        return itemRegistry.get(itemId);
    }

    public Item getNMSItem(String itemId) {
        return nmsItemRegistry.get(itemId);
    }

    public AbstractItem<? extends ItemMeta> getWeightedRandomItem(Map<AbstractItem<? extends ItemMeta>, Integer> adjustments) {
        List<AbstractItem<? extends ItemMeta>> list = new ArrayList<>(itemRegistry.values());
        Collections.shuffle(list);

        int totalWeight = list.stream()
            .mapToInt(item -> adjustments.getOrDefault(item, item.getWeight()))
            .sum();
        int randomWeight = Util.randomInt(0, totalWeight);

        for (AbstractItem<? extends ItemMeta> item : list) {
            int weight = adjustments.getOrDefault(item, item.getWeight());
            randomWeight -= weight;

            if (randomWeight <= 0) {
                return item;
            }
        }

        plugin.logLoudly("Failed to get weighted random item");
        return null;
    }

    private void registerItem(AbstractItem<? extends ItemMeta> item) {
        itemRegistry.put(item.getItemId(), item);
    }

    @SuppressWarnings("SameParameterValue")
    private void registerNMSItem(String id, Item item, Item override) {
        overrideItemRegistryEntry(id, item, override);

        nmsItemRegistry.put(id, item);
    }

    private void overrideItemRegistryEntry(String id, Item item, Item override) {
        ResourceKey<Item> key = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ResourceLocation.withDefaultNamespace(id));

        RegistrationInfo info = RegistrationInfo.BUILT_IN;

        Holder.Reference<Item> holder = reflectUtil.reflect(MappedRegistry.class, BuiltInRegistries.ITEM, () -> {
            Map<Item, Holder.Reference<Item>> map = reflectUtil.getFieldValue("unregisteredIntrusiveHolders");
            return map.get(item);
        });

        reflectUtil.reflect(Holder.Reference.class, holder, () -> {
            reflectUtil.invokeMethod("bindKey", new Class<?>[]{ResourceKey.class}, key);
        });

        reflectUtil.reflect(MappedRegistry.class, BuiltInRegistries.ITEM, () -> {
            HashMap<ResourceKey<Item>, Holder.Reference<Item>> byKey = reflectUtil.getFieldValue("byKey");
            byKey.put(key, holder);

            HashMap<ResourceLocation, Holder.Reference<Item>> byLocation = reflectUtil.getFieldValue("byLocation");
            byLocation.put(key.location(), holder);

            IdentityHashMap<Item, Holder.Reference<Item>> byValue = reflectUtil.getFieldValue("byValue");
            byValue.put(item, holder);

            ObjectArrayList<Holder.Reference<Item>> byId = reflectUtil.getFieldValue("byId");
            byId.set(byId.indexOf(byValue.remove(override)), holder);

            Reference2IntOpenHashMap<Item> toId = reflectUtil.getFieldValue("toId");
            toId.put(item, toId.getInt(override));
            toId.removeInt(override);

            IdentityHashMap<ResourceKey<Item>, RegistrationInfo> registrationInfos = reflectUtil.getFieldValue("registrationInfos");
            registrationInfos.put(key, info);

            Lifecycle lifecycle = reflectUtil.getFieldValue("registryLifecycle");
            reflectUtil.setFieldValue("registryLifecycle", lifecycle.add(info.lifecycle()));

            Map<Item, Holder.Reference<Item>> unregisteredHolders = reflectUtil.getFieldValue("unregisteredIntrusiveHolders");
            unregisteredHolders.remove(item);
        });
    }
}
