package net.themcbrothers.lib.energy;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Implementation of {@link IEnergyStorage} for items that hold energy.
 */
public class EnergyConversionStorage implements IEnergyStorage {
    public ItemStack itemStack;
    public EnergyContainerItem energyItem;

    public EnergyConversionStorage(EnergyContainerItem energyItem, ItemStack itemStack) {
        this.itemStack = itemStack;
        this.energyItem = energyItem;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return this.energyItem.receiveEnergy(itemStack, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return this.energyItem.extractEnergy(itemStack, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return this.energyItem.getEnergyStored(itemStack);
    }

    @Override
    public int getMaxEnergyStored() {
        return this.energyItem.getMaxEnergyStored(itemStack);
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
