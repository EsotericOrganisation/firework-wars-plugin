package net.slqmy.firework_wars_plugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ItemBuilder<M extends ItemMeta> {
  private static final MiniMessage MM = MiniMessage.miniMessage();

  private final FireworkWarsPlugin plugin;
  private final PersistentDataManager pdcManager;

  private Material material;
  private int amount;

  private String name;
  private TextColor nameColour;
  private List<String> lore;

  private boolean enchanted;
  private boolean unbreakable;

  private String customStringData;
  private Boolean customBooleanData;

  private Consumer<M> metaModifier;
  private Supplier<ItemStack> itemSupplier;

  public ItemBuilder(FireworkWarsPlugin plugin) {
    this.plugin = plugin;
    this.pdcManager = plugin.getPdcManager();

    this.material = Material.AIR;
    this.amount = 1;
    this.lore = new ArrayList<>();
  }

  public ItemBuilder(FireworkWarsPlugin plugin, Material material) {
    this.plugin = plugin;
    this.pdcManager = plugin.getPdcManager();

    this.material = material;
    this.amount = 1;
    this.lore = new ArrayList<>();
  }

  public ItemBuilder<M> setMaterial(Material material) {
    this.material = material;
    return this;
  }

  public Material getMaterial() {
    return material;
  }

  public ItemBuilder<M> setAmount(int amount) {
    this.amount = amount;
    return this;
  }

  public int getAmount() {
    return amount;
  }

  public ItemBuilder<M> setName(String name) {
    this.name = name;
    return this;
  }

  public ItemBuilder<M> setName(Component name) {
    this.name = MM.serialize(name);
    this.nameColour = name.color();
    return this;
  }

  public String getName() {
    return name;
  }

  public ItemBuilder<M> setNameColour(TextColor nameColour) {
    this.nameColour = nameColour;
    return this;
  }

  public TextColor getNameColour() {
    return nameColour;
  }

  public ItemBuilder<M> addLoreLines(String... lore) {
    this.lore.addAll(List.of(lore));
    return this;
  }

  public ItemBuilder<M> addLoreLines(Component... lore) {
    this.lore.addAll(Stream.of(lore).map(MM::serialize).toList());
    return this;
  }

  public ItemBuilder<M> setLore(String... lore) {
    this.lore = new ArrayList<>(List.of(lore));
    return this;
  }

  public ItemBuilder<M> setLore(Component... lore) {
    this.lore = Stream.of(lore).map(MM::serialize).toList();
    return this;
  }

  public List<String> getLore() {
    return lore;
  }

  public ItemBuilder<M> setEnchanted(boolean enchanted) {
    this.enchanted = enchanted;
    return this;
  }

  public boolean isEnchanted() {
    return enchanted;
  }

  public ItemBuilder<M> setUnbreakable(boolean unbreakable) {
    this.unbreakable = unbreakable;
    return this;
  }

  public boolean isUnbreakable() {
    return unbreakable;
  }

  public ItemBuilder<M> setCustomStringData(String customStringData) {
    this.customStringData = customStringData;
    return this;
  }

  public String getCustomStringData() {
    return customStringData;
  }

  public ItemBuilder<M> setCustomBooleanData(Boolean customBooleanData) {
    this.customBooleanData = customBooleanData;
    return this;
  }

  public Boolean getCustomBooleanData() {
    return customBooleanData;
  }

  public ItemBuilder<M> modifyMeta(Consumer<M> metaModifier) {
    this.metaModifier = metaModifier;
    return this;
  }

  public ItemBuilder<M> itemSupplier(Supplier<ItemStack> supplier) {
    this.itemSupplier = supplier;
    return this;
  }

  @SuppressWarnings("unchecked")
  public ItemStack build() {
    ItemStack item;

    if (this.itemSupplier != null) {
      item = itemSupplier.get();
    } else {
      item = new ItemStack(material, amount);
    }

    ItemMeta meta = item.getItemMeta();
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

    if (name != null) {
      Component text = nonItalicDeserialize(name);
      meta.displayName(text);

      if (nameColour != null)
        meta.displayName(text.color(nameColour));
    }

    if (!lore.isEmpty())
      meta.lore(lore.stream().map(this::nonItalicDeserialize).toList());

    if (enchanted)
      addEnchantGlint(meta);

    if (unbreakable)
      meta.setUnbreakable(true);

    if (customStringData != null)
      addStringData(meta, customStringData);

    if (customBooleanData != null)
      addBooleanData(meta, customBooleanData);

    if (metaModifier != null)
      metaModifier.accept((M) meta);

    item.setItemMeta(meta);

    return item;
  }

  protected void addEnchantGlint(ItemMeta meta) {
    meta.addEnchant(Enchantment.UNBREAKING, 1, true);
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
  }

  protected void addStringData(ItemMeta meta, String data) {
    pdcManager.setStringValue(meta, "custom_item_data", data);
  }

  protected void addBooleanData(ItemMeta meta, boolean data) {
    pdcManager.setBooleanValue(meta, "custom_item_data", data);
  }

  protected Component nonItalicDeserialize(String message) {
    return MM.deserialize("<!i>" + message);
  }
}
