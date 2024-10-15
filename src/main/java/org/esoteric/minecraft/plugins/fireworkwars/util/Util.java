package org.esoteric.minecraft.plugins.fireworkwars.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Util {
    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
    }

    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static boolean testInventory(Inventory inventory, Predicate<ItemStack> predicate) {
        return Stream
                .of(inventory.getContents())
                .filter(Objects::nonNull)
                .anyMatch(predicate);
    }

    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public static double randomDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public static boolean randomChance(double chance) {
        return Math.random() < chance;
    }

    public static boolean randomChance() {
        return randomChance(0.5D);
    }

    public static List<Integer> orderedNumberList(int start, int end) {
        List<Integer> list = new ArrayList<>();

        for (int i = start; i <= end; i++) {
            list.add(i);
        }

        return list;
    }
}
