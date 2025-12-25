package net.themcbrothers.lib.client.render.fluidtank;

import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public record FluidTank(@Nullable FluidStack fluid, int capacity) {
}
