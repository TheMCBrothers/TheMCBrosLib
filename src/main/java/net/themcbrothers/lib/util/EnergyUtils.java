package net.themcbrothers.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Helps with {@link IEnergyStorage}
 */
public final class EnergyUtils {
    private EnergyUtils() {
    }

    /**
     * Get an energy storage from a block entity at a specific pos
     *
     * @param level Level
     * @param pos   Block pos
     * @param side  Optional side
     * @return Energy Storage
     */
    public static IEnergyStorage getEnergy(Level level, BlockPos pos, @Nullable Direction side) {
        return level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
    }
}
