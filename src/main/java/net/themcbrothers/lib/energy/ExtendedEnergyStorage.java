package net.themcbrothers.lib.energy;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Extended implementation of {@link SimpleEnergyHandler}
 */
public class ExtendedEnergyStorage extends SimpleEnergyHandler {
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

    public void setCapacity(int capacity) {
        this.set(Math.min(this.energy, capacity));
        this.capacity = capacity;
    }

    /**
     * Removes energy from the storage
     *
     * @param energy Amount of energy
     */
    public void consumeEnergy(int energy) {
        try (Transaction tx = Transaction.openRoot()) {
            this.extract(energy, tx);
            tx.commit();
        }
    }

    /**
     * Adds energy to the storage
     *
     * @param energy Amount of energy
     */
    public void growEnergy(int energy) {
        try (Transaction tx = Transaction.openRoot()) {
            this.insert(energy, tx);
            tx.commit();
        }
    }

    public int getMaxInsert() {
        return this.maxInsert;
    }

    public int getMaxExtract() {
        return this.maxExtract;
    }
}
