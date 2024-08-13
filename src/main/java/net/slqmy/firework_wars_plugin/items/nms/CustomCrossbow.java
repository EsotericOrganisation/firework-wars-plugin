package net.slqmy.firework_wars_plugin.items.nms;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.util.Keys;
import net.slqmy.firework_wars_plugin.util.PersistentDataManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
public class CustomCrossbow extends CrossbowItem {

    public static final Item.Properties PROPERTIES = new Item.Properties()
      .stacksTo(1)
      .durability(465)
      .component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);

    public CustomCrossbow(Item.Properties settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return this::isValidAmmoItem;
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return this.getAllSupportedProjectiles();
    }

    @SuppressWarnings("UnstableApiUsage")
    private boolean isValidAmmoItem(ItemStack stack) {
        FireworkWarsPlugin plugin = FireworkWarsPlugin.getInstance();

        if (plugin == null) {
            Bukkit.getLogger().severe("Failed to get FireworkWars plugin instance!");
            return super.getAllSupportedProjectiles().test(stack);
        }

        PersistentDataManager pdcManager = plugin.getPdcManager();

        org.bukkit.inventory.ItemStack bukkitStack = stack.asBukkitCopy();
        ItemMeta meta = bukkitStack.getItemMeta();

        if (meta == null) {
            return false;
        }

        if (!pdcManager.hasKey(meta, Keys.ITEM_OWNER_UUID)) {
            return false;
        }

        if (!pdcManager.hasKey(meta, Keys.CUSTOM_ITEM_ID)) {
            return false;
        }

        UUID ownerUUID = UUID.fromString(pdcManager.getStringValue(meta, Keys.ITEM_OWNER_UUID));
        Player owner = plugin.getServer().getPlayer(ownerUUID);
        assert owner != null;

        org.bukkit.inventory.ItemStack mainHandItem = owner.getInventory().getItemInMainHand();
        org.bukkit.inventory.ItemStack offHandItem = owner.getInventory().getItemInOffHand();

        String ammoId = pdcManager.getStringValue(meta, Keys.CUSTOM_ITEM_ID);

        if (!mainHandItem.isEmpty() && toNMS(mainHandItem).is(this)) {
            String acceptedAmmoId = pdcManager.getStringValue(mainHandItem.getItemMeta(), Keys.GUN_ACCEPTED_AMMO_ID);
            return ammoId.equals(acceptedAmmoId);
        } else if (!offHandItem.isEmpty() && toNMS(offHandItem).is(this)) {
            String acceptedAmmoId = pdcManager.getStringValue(offHandItem.getItemMeta(), Keys.GUN_ACCEPTED_AMMO_ID);
            return ammoId.equals(acceptedAmmoId);
        } else {
            return false;
        }
    }

    private ItemStack toNMS(org.bukkit.inventory.ItemStack stack) {
        return CraftItemStack.asNMSCopy(stack);
    }
}
