package org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder;

import org.esoteric_organisation.firework_wars_plugin.arena.json.mini_components.PlayerLocation;

import java.util.List;

@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class SupplyDropData {
    private List<PlayerLocation> supplyDropLocations = List.of();
    private int supplyDropIntervalTicks;
    private int supplyDropIntervalRandomness;

    public List<PlayerLocation> getSupplyDropLocations() {
        return supplyDropLocations;
    }

    public PlayerLocation getRandomLocation() {
        return supplyDropLocations.get((int) (Math.random() * supplyDropLocations.size()));
    }

    public int getSupplyDropIntervalTicks() {
        return supplyDropIntervalTicks;
    }

    public int getSupplyDropIntervalRandomness() {
        return supplyDropIntervalRandomness;
    }

    public int getLongestInterval() {
        return supplyDropIntervalTicks + supplyDropIntervalRandomness;
    }
}
