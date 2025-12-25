package net.themcbrothers.lib.client.render.fluidtank;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public class FluidTankRenderState extends BlockEntityRenderState {
    public @Nullable FluidStack fluid;
    public int capacity;
}
