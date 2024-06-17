package net.themcbrothers.lib.energy;

import net.minecraft.world.item.Item;

public class BasicEnergyContainerItem extends Item implements EnergyContainerItem {
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public BasicEnergyContainerItem(int capacity, Properties builder) {
        this(capacity, capacity, builder);
    }

    public BasicEnergyContainerItem(int capacity, int maxTransfer, Properties builder) {
        this(capacity, maxTransfer, maxTransfer, builder);
    }

    public BasicEnergyContainerItem(int capacity, int maxReceive, int maxExtract, Properties builder) {
        super(builder);
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int getMaxReceive() {
        return this.maxReceive;
    }

    @Override
    public int getMaxExtract() {
        return this.maxExtract;
    }
}
