package org.esoteric.minecraft.plugins.fireworkwars.items;

public enum ItemType {
    ARMOR(0.8D),
    CONSUMABLE(0.6D),
    EXPLOSIVE(0.3D),
    GUN(1.0D),
    AMMO(0.2D),
    MISC(0.5D);

    private final double maxChestValue;

    ItemType(double maxChestValue) {
        this.maxChestValue = maxChestValue;
    }

    public double getMaxChestPercentage() {
        return maxChestValue;
    }
}
