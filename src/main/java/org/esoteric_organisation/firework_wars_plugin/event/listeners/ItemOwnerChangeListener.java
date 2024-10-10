package org.esoteric_organisation.firework_wars_plugin.event.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.util.Keys;
import org.esoteric_organisation.firework_wars_plugin.util.PersistentDataManager;

public class ItemOwnerChangeListener implements Listener {
    private final FireworkWarsPlugin plugin;
    private final PersistentDataManager pdcManager;

    public ItemOwnerChangeListener(FireworkWarsPlugin plugin) {
        this.plugin = plugin;
        this.pdcManager = plugin.getPdcManager();
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Bukkit.broadcastMessage("inside item pickup event");

        checkAmmoOwner(item, player);
        updateItemLocale(item, player);
    }

    @EventHandler
    public void onItemMoveInventory(InventoryMoveItemEvent event) {
        ItemStack item = event.getItem();

        Bukkit.broadcastMessage("inside item move inventory event");

        if (!(event.getDestination().getHolder() instanceof Player player)) {
            return;
        }

        Bukkit.broadcastMessage("inside item move inventory event fully");

        checkAmmoOwner(item, player);
        updateItemLocale(item, player);
    }

    private void checkAmmoOwner(ItemStack item, Player player) {
        if (pdcManager.hasKey(item.getItemMeta(), Keys.AMMO_OWNER_UUID)) {
            Bukkit.broadcastMessage("identified as ammo");
            item.editMeta(meta -> pdcManager.setUUIDValue(meta, Keys.AMMO_OWNER_UUID, player.getUniqueId()));
        }
    }

    private void updateItemLocale(ItemStack item, Player player) {
        if (pdcManager.hasKey(item.getItemMeta(), Keys.CUSTOM_ITEM_ID)) {
            if (item.getType() == Material.CROSSBOW) {
                return; // Easter egg
            }

            String itemId = pdcManager.getStringValue(item.getItemMeta(), Keys.CUSTOM_ITEM_ID);
            ItemStack newItem = plugin.getCustomItemManager().getItem(itemId).getItem(player);

            item.editMeta(meta -> {
                meta.displayName(newItem.getItemMeta().displayName());
                meta.lore(newItem.getItemMeta().lore());
            });
        }
    }
}
