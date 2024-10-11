package org.esoteric.minecraft.plugins.fireworkwars.util;

import org.bukkit.NamespacedKey;

public final class Keys {
    public static final NamespacedKey CUSTOM_ITEM_ID = fromString("custom_item_id");
    public static final NamespacedKey AMMO_OWNER_UUID = fromString("ammo_owner_uuid");
    public static final NamespacedKey GUN_ACCEPTED_AMMO_ID = fromString("gun_accepted_ammo_id");
    public static final NamespacedKey PLAYER_COMPASS_ID = fromString("player_compass_id");
    public static final NamespacedKey EXPLOSIVE_ROCKET_ENTITY = fromString("explosive_rocket_key");

    private Keys() {
    }

    public static NamespacedKey fromString(String key) {
        return new NamespacedKey("firework-wars-plugin", key);
    }
}
