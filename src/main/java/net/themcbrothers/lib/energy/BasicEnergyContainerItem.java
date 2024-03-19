package net.themcbrothers.lib.energy;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.themcbrothers.lib.LibDataComponents;

public class BasicEnergyContainerItem extends Item implements EnergyContainerItem {
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public BasicEnergyContainerItem(Properties builder) {
        super(builder);
    }

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

    public BasicEnergyContainerItem setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public BasicEnergyContainerItem setMaxTransfer(int maxTransfer) {
        setMaxExtract(maxTransfer);
        setMaxReceive(maxTransfer);
        return this;
    }

    public BasicEnergyContainerItem setMaxExtract(int maxExtract) {
        this.maxExtract = maxExtract;
        return this;
    }

    public BasicEnergyContainerItem setMaxReceive(int maxReceive) {
        this.maxReceive = maxReceive;
        return this;
    }

    /* IEnergyContainerItem */
    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        int stored = Math.min(getEnergyStored(container), getMaxEnergyStored(container));
        int energyReceived = Math.min(this.capacity - stored, Math.min(this.maxReceive, maxReceive));

        if (!simulate) {
            stored += energyReceived;
            container.set(LibDataComponents.ENERGY.get(), stored);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (!container.has(LibDataComponents.ENERGY.get())) {
            return 0;
        }

        int stored = Math.min(getEnergyStored(container), getMaxEnergyStored(container));
        int energyExtracted = Math.min(stored, Math.min(this.maxExtract, maxExtract));

        if (!simulate) {
            stored -= energyExtracted;
            container.set(LibDataComponents.ENERGY.get(), stored);
        }

        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return container.getOrDefault(LibDataComponents.ENERGY.get(), 0);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return this.capacity;
    }
}
