package net.themcbrothers.lib.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

/**
 * Extended implementation of {@link EnergyStorage}
 */
public class ExtendedEnergyStorage extends EnergyStorage {
    public ExtendedEnergyStorage(int capacity) {
        super(capacity);
    }

    public ExtendedEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public ExtendedEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public ExtendedEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }

    public void setMaxEnergyStored(int energy) {
        this.capacity = energy;
    }

    /**
     * Removes energy from the storage
     *
     * @param energy Amount of energy
     */
    public void consumeEnergy(int energy) {
        this.energy -= energy;

        if (this.energy < 0) {
            this.energy = 0;
        }
    }

    /**
     * Adds energy to the storage
     *
     * @param energy Amount of energy
     */
    public void growEnergy(int energy) {
        this.energy += energy;

        if (this.energy > this.capacity) {
            this.energy = this.capacity;
        }
    }

    public int getMaxReceive() {
        return this.maxReceive;
    }

    public int getMaxExtract() {
        return this.maxExtract;
    }
}
