package net.themcbrothers.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Helps with {@link EnergyHandler}
 */
public final class EnergyUtils {
    private EnergyUtils() {
    }

    /**
     * Get an energy storage from a block entity at a specific position
     *
     * @param level Level
     * @param pos   Block position
     * @param side  Nullable side
     * @return Energy Storage as {@link Optional}
     */
    public static Optional<EnergyHandler> getEnergy(Level level, BlockPos pos, @Nullable Direction side) {
        return Optional.ofNullable(level.getCapability(Capabilities.Energy.BLOCK, pos, side));
    }

    /**
     * Get an energy storage from an {@link Entity}
     *
     * @param entity Entity
     * @param side   Nullable side
     * @return Energy Storage as {@link Optional}
     */
    public static Optional<EnergyHandler> getEnergy(Entity entity, @Nullable Direction side) {
        return Optional.ofNullable(entity.getCapability(Capabilities.Energy.ENTITY, side));
    }

    /**
     * Get an energy storage from an {@link ItemStack}
     *
     * @param stack Item Stack
     * @return Energy Storage as {@link Optional}
     */
    public static Optional<EnergyHandler> getEnergy(ItemStack stack) {
        return Optional.ofNullable(ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM));
    }
}
