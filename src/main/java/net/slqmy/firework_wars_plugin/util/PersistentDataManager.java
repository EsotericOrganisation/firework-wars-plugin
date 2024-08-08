package net.slqmy.firework_wars_plugin.util;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("unused")
public class PersistentDataManager {
  private final FireworkWarsPlugin plugin;

  public PersistentDataManager(FireworkWarsPlugin plugin) {
    this.plugin = plugin;
  }

  public boolean hasKey(PersistentDataHolder holder, NamespacedKey key) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    return pdc.has(key);
  }

  public boolean hasKey(PersistentDataHolder holder, String key) {
      PersistentDataContainer pdc = holder.getPersistentDataContainer();
      return pdc.has(this.fromString(key));
  }

  public String getStringValue(PersistentDataHolder holder, NamespacedKey key) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    return pdc.get(key, PersistentDataType.STRING);
  }

  public String getStringValue(PersistentDataHolder holder, String key) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    return pdc.get(this.fromString(key), PersistentDataType.STRING);
  }

  public Boolean getBooleanValue(PersistentDataHolder holder, NamespacedKey key) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    return pdc.get(key, PersistentDataType.BOOLEAN);
  }

  public Boolean getBooleanValue(PersistentDataHolder holder, String key) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    return pdc.get(this.fromString(key), PersistentDataType.BOOLEAN);
  }

  public Integer getIntValue(PersistentDataHolder holder, NamespacedKey key) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    return pdc.get(key, PersistentDataType.INTEGER);
  }

  public Integer getIntValue(PersistentDataHolder holder, String key) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    return pdc.get(this.fromString(key), PersistentDataType.INTEGER);
  }

  public int[] getIntListValue(PersistentDataHolder holder, NamespacedKey key) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    return pdc.get(key, PersistentDataType.INTEGER_ARRAY);
  }

  public int[] getIntListValue(PersistentDataHolder holder, String key) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    return pdc.get(this.fromString(key), PersistentDataType.INTEGER_ARRAY);
  }

  public void setStringValue(PersistentDataHolder holder, NamespacedKey key, String value) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    pdc.set(key, PersistentDataType.STRING, value);
  }

  public void setStringValue(PersistentDataHolder holder, String key, String value) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    pdc.set(this.fromString(key), PersistentDataType.STRING, value);
  }

  public void setBooleanValue(PersistentDataHolder holder, NamespacedKey key, Boolean value) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    pdc.set(key, PersistentDataType.BOOLEAN, value);
  }

  public void setBooleanValue(PersistentDataHolder holder, String key, Boolean value) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    pdc.set(this.fromString(key), PersistentDataType.BOOLEAN, value);
  }

  public void setIntValue(PersistentDataHolder holder, NamespacedKey key, Integer value) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    pdc.set(key, PersistentDataType.INTEGER, value);
  }

  public void setIntValue(PersistentDataHolder holder, String key, Integer value) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    pdc.set(this.fromString(key), PersistentDataType.INTEGER, value);
  }

  public void setIntListValue(PersistentDataHolder holder, NamespacedKey key, int[] value) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    pdc.set(key, PersistentDataType.INTEGER_ARRAY, value);
  }

  public void setIntListValue(PersistentDataHolder holder, String key, int[] value) {
    PersistentDataContainer pdc = holder.getPersistentDataContainer();
    pdc.set(this.fromString(key), PersistentDataType.INTEGER_ARRAY, value);
  }

  private NamespacedKey fromString(String key) {
    return new NamespacedKey(this.plugin, key);
  }
}
