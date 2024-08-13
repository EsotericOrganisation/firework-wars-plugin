package net.slqmy.firework_wars_plugin.util;

import org.bukkit.NamespacedKey;

public final class Keys {
    public static final NamespacedKey CUSTOM_ITEM_ID = fromString("custom_item_id");
    public static final NamespacedKey ITEM_OWNER_UUID = fromString("item_owner_uuid");
    public static final NamespacedKey GUN_ACCEPTED_AMMO_ID = fromString("gun_accepted_ammo_id");

    private Keys() {}

    public static NamespacedKey fromString(String key) {
        return new NamespacedKey("firework_wars_plugin", key);
    }
}
