package net.themcbrothers.lib.client.render.fluidtank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class FluidTankSpecialRenderer implements SpecialModelRenderer<FluidTank> {
    private final FluidTankRenderer fluidTankRenderer;

    public FluidTankSpecialRenderer(FluidTankRenderer fluidTankRenderer) {
        this.fluidTankRenderer = fluidTankRenderer;
    }

    @Override
    public void submit(@Nullable FluidTank argument, ItemDisplayContext type, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (argument != null) {
            this.fluidTankRenderer.submitTank(argument.fluid(), argument.capacity(), poseStack, submitNodeCollector, lightCoords);
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
    }

    @Override
    public @Nullable FluidTank extractArgument(ItemStack stack) {
        ResourceHandler<FluidResource> handler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);

        if (handler == null || handler.size() != 1) {
            return new FluidTank(null, 0);
        }

        return new FluidTank(FluidUtil.getStack(handler, 0), handler.getCapacityAsInt(0, FluidResource.EMPTY));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<FluidTankSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new FluidTankSpecialRenderer.Unbaked());

        @Override
        public MapCodec<FluidTankSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
            return new FluidTankSpecialRenderer(new FluidTankRenderer(context));
        }
    }
}
