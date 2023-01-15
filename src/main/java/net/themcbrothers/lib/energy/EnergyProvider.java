package net.themcbrothers.lib.energy;

/**
 * For containers
 */
public interface EnergyProvider {
    long getEnergyStored();

    long getMaxEnergyStored();
}
