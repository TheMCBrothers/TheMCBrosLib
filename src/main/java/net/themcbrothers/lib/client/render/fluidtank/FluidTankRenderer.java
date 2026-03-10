package net.themcbrothers.lib.client.render.fluidtank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.themcbrothers.lib.client.model.fluid.FluidCuboid;
import net.themcbrothers.lib.client.render.FluidRenderer;
import net.themcbrothers.lib.fluidtank.FluidTankBlockEntity;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import static net.themcbrothers.lib.client.render.FluidRenderer.getStillFluidSprite;

public class FluidTankRenderer implements BlockEntityRenderer<FluidTankBlockEntity, FluidTankRenderState> {
    private static final FluidCuboid CUBOID = new FluidCuboid(new Vector3f(1.01F, 1.01F, 1.01F), new Vector3f(14.99F, 14.99F, 14.99F), FluidCuboid.DEFAULT_FACES);

    public FluidTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    public FluidTankRenderer(SpecialModelRenderer.BakingContext context) {
    }

    @Override
    public FluidTankRenderState createRenderState() {
        return new FluidTankRenderState();
    }

    @Override
    public void extractRenderState(FluidTankBlockEntity blockEntity, FluidTankRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.fluid = FluidUtil.getStack(blockEntity.getTank(), 0);
        state.capacity = blockEntity.getTank().getCapacityAsInt(0, FluidResource.EMPTY);
    }

    @Override
    public void submit(FluidTankRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        this.submitTank(state.fluid, state.capacity, poseStack, submitNodeCollector, state.lightCoords);
    }

    public void submitTank(@Nullable FluidStack fluidStack, int capacity, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords) {
        Identifier texture = getStillFluidSprite(fluidStack).atlasLocation();
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(texture), (pose, buffer) -> {
            this.renderTank(pose, buffer, CUBOID, fluidStack, capacity, lightCoords);
        });
    }

    public void renderTank(PoseStack.Pose pose, VertexConsumer buffer, FluidCuboid cuboid, @Nullable FluidStack fluidStack, int capacity, int lightCoords) {
        if (fluidStack == null || fluidStack.isEmpty()) {
            return;
        }

        FluidRenderer.renderScaledCuboid(pose, buffer, cuboid, fluidStack, 0F, capacity, lightCoords, fluidStack.getFluidType().isLighterThanAir());
    }
}
