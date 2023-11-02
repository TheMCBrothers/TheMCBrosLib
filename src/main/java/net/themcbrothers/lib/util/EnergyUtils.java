package net.themcbrothers.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capabilities;
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
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity != null) {
            return blockEntity.getCapability(Capabilities.ENERGY, side).map(energyStorage -> energyStorage).orElse(null);
        }

        return null;
    }
}
