package net.slqmy.firework_wars_plugin.items;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.items.manager.AbstractItem;
import net.slqmy.firework_wars_plugin.language.Message;
import net.slqmy.firework_wars_plugin.util.ItemBuilder;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class RifleAmmo extends AbstractItem {

    public RifleAmmo(FireworkWarsPlugin plugin) {
        super(plugin, "firework_rifle_ammo", Material.FIREWORK_ROCKET);
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder<FireworkMeta>(plugin, itemMaterial)
            .setName(plugin.getLanguageManager().getMessage(Message.FIREWORK_RIFLE_AMMO, player))
            .setLore(plugin.getLanguageManager().getMessage(Message.FIREWORK_RIFLE_AMMO_LORE, player))
            .build();
    }
}
