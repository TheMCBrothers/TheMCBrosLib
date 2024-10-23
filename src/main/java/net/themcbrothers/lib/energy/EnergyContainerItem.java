package net.themcbrothers.lib.energy;

import net.minecraft.world.item.ItemStack;
import net.themcbrothers.lib.LibDataComponents;

/**
 * Implement this interface on Item classes that support external manipulation of their internal energy storages.
 * <p>
 * Example {@link BasicEnergyContainerItem}
 */
public interface EnergyContainerItem {
    /**
     * Returns the energy stored in the container
     */
    default int getEnergyStored(ItemStack container) {
        return container.getOrDefault(LibDataComponents.ENERGY, 0);
    }

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
