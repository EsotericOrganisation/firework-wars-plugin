package org.esoteric.minecraft.plugins.games.firework.wars.items.consumables;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.esoteric.minecraft.plugins.games.firework.wars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.games.firework.wars.items.AbstractItem;
import org.esoteric.minecraft.plugins.games.firework.wars.items.ItemType;
import org.esoteric.minecraft.plugins.games.firework.wars.language.Message;
import org.esoteric.minecraft.plugins.games.firework.wars.util.ItemBuilder;
import org.esoteric.minecraft.plugins.games.firework.wars.util.Util;

public class GoldenAppleItem extends AbstractItem<ItemMeta> {
    public GoldenAppleItem(FireworkWarsPlugin plugin) {
        super(plugin, "golden_apple", Material.GOLDEN_APPLE, 60, 8, ItemType.CONSUMABLE);
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder<>(plugin, itemMaterial)
            .setName(Message.GOLDEN_APPLE, player)
            .setLore(Message.GOLDEN_APPLE_LORE, player)
            .modifyMeta(this::modifyMeta)
            .build();
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    protected void modifyMeta(ItemMeta meta) {
        super.modifyMeta(meta);

        FoodComponent foodComponent = meta.getFood();
        foodComponent.setNutrition(4);
        foodComponent.setCanAlwaysEat(true);
        foodComponent.setEatSeconds(1.2F);
        foodComponent.setSaturation(14.4F);

        foodComponent.addEffect(
            new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1), 1.0F);
        foodComponent.addEffect(
            new PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 0), 1.0F);
        foodComponent.addEffect(
            new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1), 1.0F);
        foodComponent.addEffect(
            new PotionEffect(PotionEffectType.RESISTANCE, 35, 1, false, false, false), 1.0F);

        meta.setFood(foodComponent);
    }

    @Override
    public int getStackAmount() {
        return Util.randomInt(1, 2);
    }
}
