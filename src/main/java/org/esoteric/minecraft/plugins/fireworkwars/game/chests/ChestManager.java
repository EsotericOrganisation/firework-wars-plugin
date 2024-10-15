package org.esoteric.minecraft.plugins.fireworkwars.game.chests;

import org.bukkit.block.Chest;
import org.bukkit.inventory.meta.ItemMeta;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.components.ChestLocation;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.structure.Arena;
import org.esoteric.minecraft.plugins.fireworkwars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.fireworkwars.items.AbstractItem;
import org.esoteric.minecraft.plugins.fireworkwars.items.CustomItemManager;
import org.esoteric.minecraft.plugins.fireworkwars.items.ItemType;
import org.esoteric.minecraft.plugins.fireworkwars.util.Util;

import java.util.*;

import static java.util.Comparator.comparingInt;

public class ChestManager {
    private final Arena arena;
    private final CustomItemManager itemManager;

    public ChestManager(FireworkWarsPlugin plugin, FireworkWarsGame game) {
        this.arena = game.getArena();
        this.itemManager = plugin.getCustomItemManager();
    }

    public void refillChests(double valueFactor) {
        for (ChestLocation chestLocation : arena.getChestLocations()) {
            Chest chest = (Chest) chestLocation.getChestBlock().getState();

            int maxTotalValue = (int) (chestLocation.getMaxTotalValue() * valueFactor);
            int maxItemValue = (int) (chestLocation.getMaxValuePerItem() * valueFactor);

            List<AbstractItem<? extends ItemMeta>> itemsToAdd = new ArrayList<>();

            this.addRandomItemsToList(
                itemsToAdd,
                maxItemValue, maxTotalValue,
                chest.getInventory().getSize(),
                new HashMap<>(), new EnumMap<>(ItemType.class));

            if (itemsToAdd.isEmpty()) {
                return;
            }

            this.addItemsToChest(itemsToAdd, chest, maxTotalValue);
        }
    }

    private void addRandomItemsToList(List<AbstractItem<? extends ItemMeta>> itemList, int maxItemValue, int maxTotalValue, int maxChestCapacity, Map<AbstractItem<? extends ItemMeta>, Integer> weightAdjustments, Map<ItemType, Integer> weightPerItemType) {
        int i = 0;
        while (i < maxTotalValue && itemList.size() < maxChestCapacity) {
            AbstractItem<? extends ItemMeta> item = itemManager.getWeightedRandomItem(weightAdjustments);

            if (item.getValue() > maxItemValue) {
                i++;
                continue;
            }

            ItemType type = item.getType();
            int newTotalTypeWeight = weightPerItemType.getOrDefault(type, 0) + item.getValue();

            if (newTotalTypeWeight > type.getMaxChestPercentage() * maxTotalValue) {
                i++;
                continue;
            }

            itemList.add(item);
            i += item.getValue();

            if (item.isConsumable() || item.isAmmo()) {
                weightAdjustments.put(
                    item, (int) Math.floor(weightAdjustments.getOrDefault(item, item.getWeight()) * 0.65F));
            } else {
                weightAdjustments.put(
                    item, (int) Math.floor(weightAdjustments.getOrDefault(item, item.getWeight()) / 2.0F));
            }

            weightPerItemType.put(type, newTotalTypeWeight);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void addItemsToChest(List<AbstractItem<? extends ItemMeta>> itemList, Chest chest, int maxTotalValue) {
        List<Integer> slots = Util.orderedNumberList(0, chest.getInventory().getSize() - 1);
        Collections.shuffle(slots);

        if (Util.randomChance(0.4D)) {
            AbstractItem<? extends ItemMeta> item = itemList.stream()
                .max(comparingInt(AbstractItem::getValue))
                .get();

            this.putItemInChest(item, chest, slots.removeFirst());
            itemList.remove(item);
        }

        int i = 0;
        while (i < maxTotalValue) {
            if (itemList.isEmpty()) {
                break;
            }

            AbstractItem<? extends ItemMeta> item = itemList.removeFirst();

            if (i + item.getValue() > maxTotalValue) {
                continue;
            }

            this.putItemInChest(item, chest, slots.removeFirst());
            i += item.getValue();
        }
    }

    private void putItemInChest(AbstractItem<? extends ItemMeta> item, Chest chest, int slot) {
        chest.getInventory().setItem(slot, item.getItem(null, item.getStackAmount()));
    }
}
