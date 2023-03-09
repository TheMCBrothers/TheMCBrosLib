package net.themcbrothers.lib.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.themcbrothers.lib.client.model.fluid.FluidCuboid;
import net.themcbrothers.lib.client.render.FluidRenderer;

/**
 * Utilities to help with rendering tanks
 */
public final class RenderUtils {
    private RenderUtils() {
    }

    /**
     * Renders a fluid from a Fluid Tank
     *
     * @param poseStack Pose Stack
     * @param buffer    Multi Buffer Source
     * @param cube      Fluid Cuboid
     * @param tank      Fluid Tank
     * @param light     Light
     */
    public static void renderFluidTank(PoseStack poseStack, MultiBufferSource buffer, FluidCuboid cube, IFluidTank tank, int light) {
        // render liquid if present
        FluidStack liquid = tank.getFluid();
        int capacity = tank.getCapacity();

        if (!liquid.isEmpty() && capacity > 0) {
            // fetch fluid information from the model
            FluidRenderer.renderScaledCuboid(poseStack, buffer, cube, liquid, 0F, capacity, light, liquid.getFluid().getFluidType().isLighterThanAir());
        }
    }
}
