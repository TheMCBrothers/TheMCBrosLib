package net.themcbrothers.lib.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
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
    public static void renderFluidTank(PoseStack poseStack, MultiBufferSource buffer, FluidCuboid cube, ResourceHandler<FluidResource> tank, int light) {
        // render liquid if present
        FluidStack liquid = tank.getResource(0).toStack(tank.getAmountAsInt(0));
        int capacity = tank.getCapacityAsInt(0, tank.getResource(0));

        if (!liquid.isEmpty() && capacity > 0) {
            // fetch fluid information from the model
            FluidRenderer.renderScaledCuboid(poseStack, buffer, cube, liquid, 0F, capacity, light, liquid.getFluid().getFluidType().isLighterThanAir());
        }
    }
}
