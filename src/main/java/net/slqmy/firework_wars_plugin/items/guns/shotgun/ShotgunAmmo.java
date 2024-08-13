package net.slqmy.firework_wars_plugin.items.guns.shotgun;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.items.manager.AbstractItem;
import net.slqmy.firework_wars_plugin.language.Message;
import net.slqmy.firework_wars_plugin.util.ItemBuilder;
import net.slqmy.firework_wars_plugin.util.Keys;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShotgunAmmo extends AbstractItem {
    public ShotgunAmmo(FireworkWarsPlugin plugin) {
        super(plugin, "firework_rifle_ammo", Material.NETHER_WART);
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder<>(plugin, itemMaterial)
            .setName(plugin.getLanguageManager().getMessage(Message.FIREWORK_SHOTGUN_AMMO, player))
            .setLore(plugin.getLanguageManager().getMessage(Message.FIREWORK_SHOTGUN_AMMO_LORE, player))
            .modifyMeta(meta -> {
                pdcManager.setStringValue(meta, isItemKey, itemId);
                pdcManager.setStringValue(meta, Keys.ITEM_OWNER_UUID, player.getUniqueId().toString());
            })
            .build();
    }
}
