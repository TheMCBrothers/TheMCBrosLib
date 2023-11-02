package net.themcbrothers.lib.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.themcbrothers.lib.capability.CapabilityProvider;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new CapabilityProvider<>(new EnergyConversionStorage(this, stack), Capabilities.ENERGY, null);
    }

    /* IEnergyContainerItem */
    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        int stored = Math.min(container.getOrCreateTag().getInt(TAG_ENERGY), getMaxEnergyStored(container));
        int energyReceived = Math.min(this.capacity - stored, Math.min(this.maxReceive, maxReceive));

        if (!simulate) {
            stored += energyReceived;
            container.getOrCreateTag().putInt(TAG_ENERGY, stored);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (container.getTag() == null || !container.getTag().contains(TAG_ENERGY)) {
            return 0;
        }

        int stored = Math.min(container.getTag().getInt(TAG_ENERGY), getMaxEnergyStored(container));
        int energyExtracted = Math.min(stored, Math.min(this.maxExtract, maxExtract));

        if (!simulate) {
            stored -= energyExtracted;
            container.getTag().putInt(TAG_ENERGY, stored);
        }

        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if (container.getTag() == null || !container.getTag().contains(TAG_ENERGY)) {
            return 0;
        }

        return Math.min(container.getTag().getInt(TAG_ENERGY), getMaxEnergyStored(container));
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return this.capacity;
    }
}
