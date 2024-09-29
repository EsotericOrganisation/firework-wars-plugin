package org.esoteric_organisation.firework_wars_plugin.items.guns.rifle;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.Bukkit;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsTeam;
import org.esoteric_organisation.firework_wars_plugin.items.guns.BaseGunItem;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import java.util.List;
import java.util.function.Consumer;

public class FireworkRifleItem extends BaseGunItem {

  public FireworkRifleItem(FireworkWarsPlugin plugin) {
    super(plugin, "firework_rifle", "firework_rifle_ammo");
  }

  @Override
  public ItemStack getItem(Player player) {
    Consumer<CrossbowMeta> consumer = this::modifyMeta;
    Consumer<CrossbowMeta> modifyMeta = consumer.andThen(meta -> meta.addEnchant(Enchantment.QUICK_CHARGE, 3, true));

    return getItemBuilder().setName(languageManager.getMessage(Message.FIREWORK_RIFLE, player)).setLore(languageManager.getMessages(Message.FIREWORK_RIFLE_LORE, player))
        .itemSupplier(this::getCustomCrossbow).modifyMeta(modifyMeta).build();
  }

  @Override
  protected void onCrossbowLoad(Player player, FireworkWarsGame game, EntityLoadCrossbowEvent event) {
    FireworkWarsTeam team = game.getTeam(player);
    ItemStack firework = createFirework(team.getConfiguredTeam().getColor(), 4);

    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> event.getCrossbow().editMeta(meta -> ((CrossbowMeta) meta).setChargedProjectiles(List.of(firework))), 1L);
  }

  @Override
  protected void onCrossbowShoot(Player player, FireworkWarsGame game, EntityShootBowEvent event) {

  }
}
