package org.esoteric.minecraft.plugins.fireworkwars.items;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.language.LanguageManager;
import org.esoteric.minecraft.plugins.fireworkwars.util.Keys;
import org.esoteric.minecraft.plugins.fireworkwars.util.PersistentDataManager;
import org.jetbrains.annotations.Contract;

public abstract class AbstractItem<M extends ItemMeta> implements Listener {
    protected final FireworkWarsPlugin plugin;
    protected final MiniMessage MM;

    protected final PersistentDataManager pdcManager;
    protected final LanguageManager languageManager;

    protected final String itemId;
    protected final Material itemMaterial;

    protected final int weight;
    protected final int value;

    protected final NamespacedKey customItemIdKey;

    public AbstractItem(FireworkWarsPlugin plugin, String itemId, Material itemMaterial, int weight, int value) {
        this.plugin = plugin;
        this.MM = MiniMessage.miniMessage();

        this.pdcManager = plugin.getPdcManager();
        this.languageManager = plugin.getLanguageManager();

        this.itemId = itemId;
        this.itemMaterial = itemMaterial;

        this.weight = weight;
        this.value = value;

        this.customItemIdKey = Keys.CUSTOM_ITEM_ID;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public String getItemId() {
        return itemId;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public int getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }

    @Contract("null -> false")
    public boolean isValidCustomItem(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (!itemStack.hasItemMeta()) {
            return false;
        }

        String itemStackItemId = plugin.getPdcManager().getStringValue(itemStack.getItemMeta(), customItemIdKey);
        return itemId.equals(itemStackItemId);
    }

    protected ItemStack getBaseItemStack() {
        return new ItemStack(itemMaterial);
    }

    protected void modifyMeta(M meta) {
        pdcManager.setStringValue(meta, customItemIdKey, itemId);
    }

    public abstract ItemStack getItem(Player player);

    public ItemStack getItem(Player player, int amount) {
        ItemStack item = getItem(player);
        item.setAmount(amount);
        return item;
    }

    public abstract int getStackAmount();
}
