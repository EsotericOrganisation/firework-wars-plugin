package org.esoteric.minecraft.plugins.fireworkwars.event.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.TeamPlayer;
import org.esoteric.minecraft.plugins.fireworkwars.util.Keys;
import org.esoteric.minecraft.plugins.fireworkwars.util.PersistentDataManager;

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

        updateWoolColor(item, player);
        checkAmmoOwner(item, player);
        updateItemLocale(item, player);
    }

    @EventHandler
    public void onItemMoveInventory(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        //todo: when halving a stack, make sure the stack remaining in the chest stays white
        //todo: when putting the stuff back into the chest, turn the wool back to white

        updateWoolColor(item, player);
        checkAmmoOwner(item, player);
        updateItemLocale(item, player);
    }

    @SuppressWarnings("deprecation")
    private void updateWoolColor(ItemStack item, Player player) {
        if ("wool".equals(pdcManager.getStringValue(item.getItemMeta(), Keys.CUSTOM_ITEM_ID))) {
            TeamPlayer teamPlayer = TeamPlayer.from(player);

            if (teamPlayer != null) {
                item.setType(teamPlayer.getTeam().getWoolMaterial());
            }
        }
    }

    private void checkAmmoOwner(ItemStack item, Player player) {
        if (pdcManager.hasKey(item.getItemMeta(), Keys.AMMO_OWNER_UUID)) {
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
