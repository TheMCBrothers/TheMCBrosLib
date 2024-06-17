package net.themcbrothers.lib.energy;

/**
 * Implement this interface on Item classes that support external manipulation of their internal energy storages.
 * <p>
 * Example {@link BasicEnergyContainerItem}
 */
public interface EnergyContainerItem {
    /**
     * Returns the capacity
     */
    int getCapacity();

    /**
     * Returns the max amount that the energy storage can receive
     */
    int getMaxReceive();

    /**
     * Returns the max amount that the energy storage can extract
     */
    int getMaxExtract();
}
