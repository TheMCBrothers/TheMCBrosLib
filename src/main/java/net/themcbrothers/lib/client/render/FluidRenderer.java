package net.themcbrothers.lib.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.themcbrothers.lib.client.model.fluid.FluidCuboid;
import net.themcbrothers.lib.client.model.fluid.FluidCuboid.FluidFace;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

/**
 * Utilities for rendering fluids
 */
public class FluidRenderer {
    /**
     * Gets a block sprite from the given location
     *
     * @param sprite Sprite name
     * @return Sprite location
     */
    public static TextureAtlasSprite getBlockSprite(ResourceLocation sprite) {
        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(sprite);
    }

    /**
     * Takes the larger light value between combinedLight and the passed block light
     *
     * @param combinedLight Sky/Block light lightmap value
     * @param blockLight    New 0-15 block light value
     * @return Updated packed light including the new light value
     */
    public static int withBlockLight(int combinedLight, int blockLight) {
        // skylight from the combined plus larger block light between combined and parameter
        // not using methods from LightTexture to reduce number of operations
        return (combinedLight & 0xFFFF0000) | Math.max(blockLight << 4, combinedLight & 0xFFFF);
    }

    /* Fluid cuboids */

    /**
     * Forces the UV to be between 0 and 1
     *
     * @param value Original value
     * @param upper If true, this is the larger UV. Needed to enforce integer values end up at 1
     * @return UV mapped between 0 and 1
     */
    private static float boundUV(float value, boolean upper) {
        value = value % 1;
        if (value == 0) {
            // if it lands exactly on the 0 bound, map that to 1 instead for the larger UV
            return upper ? 1 : 0;
        }
        // modulo returns a negative result if the input is negative, so add 1 to account for that
        return value < 0 ? (value + 1) : value;
    }

    /**
     * Adds a quad to the renderer
     *
     * @param renderer   Renderer instance
     * @param matrix     Render matrix
     * @param sprite     Sprite to render
     * @param from       Quad start
     * @param to         Quad end
     * @param face       Face to render
     * @param color      Color to use in rendering
     * @param brightness Face brightness
     * @param flowing    If true, half texture coordinates
     */
    public static void putTexturedQuad(VertexConsumer renderer, Matrix4f matrix, TextureAtlasSprite sprite, Vector3f from, Vector3f to, Direction face, int color, int brightness, int rotation, boolean flowing) {
        // start with texture coordinates
        float x1 = from.x(), y1 = from.y(), z1 = from.z();
        float x2 = to.x(), y2 = to.y(), z2 = to.z();
        // choose UV based on the directions, some need to negate UV due to the direction
        // note that we use -UV instead of 1-UV as its slightly simpler and the later logic deals with negatives
        float u1, u2, v1, v2;
        switch (face) {
            default -> { // DOWN
                u1 = x1;
                u2 = x2;
                v1 = z2;
                v2 = z1;
            }
            case UP -> {
                u1 = x1;
                u2 = x2;
                v1 = -z1;
                v2 = -z2;
            }
            case NORTH -> {
                u1 = -x1;
                u2 = -x2;
                v1 = y1;
                v2 = y2;
            }
            case SOUTH -> {
                u1 = x2;
                u2 = x1;
                v1 = y1;
                v2 = y2;
            }
            case WEST -> {
                u1 = z2;
                u2 = z1;
                v1 = y1;
                v2 = y2;
            }
            case EAST -> {
                u1 = -z1;
                u2 = -z2;
                v1 = y1;
                v2 = y2;
            }
        }

        // flip V when relevant
        if (rotation == 0 || rotation == 270) {
            float temp = v1;
            v1 = -v2;
            v2 = -temp;
        }
        // flip U when relevant
        if (rotation >= 180) {
            float temp = u1;
            u1 = -u2;
            u2 = -temp;
        }

        // bound UV to be between 0 and 1
        boolean reverse = u1 > u2;
        u1 = boundUV(u1, reverse);
        u2 = boundUV(u2, !reverse);
        reverse = v1 > v2;
        v1 = boundUV(v1, reverse);
        v2 = boundUV(v2, !reverse);

        // if rotating by 90 or 270, swap U and V
        float minU, maxU, minV, maxV;
        float size = flowing ? 0.5F : 1.0F;
        if ((rotation % 180) == 90) {
            minU = sprite.getU(v1 * size);
            maxU = sprite.getU(v2 * size);
            minV = sprite.getV(u1 * size);
            maxV = sprite.getV(u2 * size);
        } else {
            minU = sprite.getU(u1 * size);
            maxU = sprite.getU(u2 * size);
            minV = sprite.getV(v1 * size);
            maxV = sprite.getV(v2 * size);
        }
        // based on rotation, put coords into place
        float u3, u4, v3, v4;
        switch (rotation) {
            default -> { // 0
                u1 = minU;
                v1 = maxV;
                u2 = minU;
                v2 = minV;
                u3 = maxU;
                v3 = minV;
                u4 = maxU;
                v4 = maxV;
            }
            case 90 -> {
                u1 = minU;
                v1 = minV;
                u2 = maxU;
                v2 = minV;
                u3 = maxU;
                v3 = maxV;
                u4 = minU;
                v4 = maxV;
            }
            case 180 -> {
                u1 = maxU;
                v1 = minV;
                u2 = maxU;
                v2 = maxV;
                u3 = minU;
                v3 = maxV;
                u4 = minU;
                v4 = minV;
            }
            case 270 -> {
                u1 = maxU;
                v1 = maxV;
                u2 = minU;
                v2 = maxV;
                u3 = minU;
                v3 = minV;
                u4 = maxU;
                v4 = minV;
            }
        }
        // add quads
        switch (face) {
            case DOWN -> {
                renderer.addVertex(matrix, x1, y1, z2).setColor(color).setUv(u1, v1).setLight(brightness);
                renderer.addVertex(matrix, x1, y1, z1).setColor(color).setUv(u2, v2).setLight(brightness);
                renderer.addVertex(matrix, x2, y1, z1).setColor(color).setUv(u3, v3).setLight(brightness);
                renderer.addVertex(matrix, x2, y1, z2).setColor(color).setUv(u4, v4).setLight(brightness);
            }
            case UP -> {
                renderer.addVertex(matrix, x1, y2, z1).setColor(color).setUv(u1, v1).setLight(brightness);
                renderer.addVertex(matrix, x1, y2, z2).setColor(color).setUv(u2, v2).setLight(brightness);
                renderer.addVertex(matrix, x2, y2, z2).setColor(color).setUv(u3, v3).setLight(brightness);
                renderer.addVertex(matrix, x2, y2, z1).setColor(color).setUv(u4, v4).setLight(brightness);
            }
            case NORTH -> {
                renderer.addVertex(matrix, x1, y1, z1).setColor(color).setUv(u1, v1).setLight(brightness);
                renderer.addVertex(matrix, x1, y2, z1).setColor(color).setUv(u2, v2).setLight(brightness);
                renderer.addVertex(matrix, x2, y2, z1).setColor(color).setUv(u3, v3).setLight(brightness);
                renderer.addVertex(matrix, x2, y1, z1).setColor(color).setUv(u4, v4).setLight(brightness);
            }
            case SOUTH -> {
                renderer.addVertex(matrix, x2, y1, z2).setColor(color).setUv(u1, v1).setLight(brightness);
                renderer.addVertex(matrix, x2, y2, z2).setColor(color).setUv(u2, v2).setLight(brightness);
                renderer.addVertex(matrix, x1, y2, z2).setColor(color).setUv(u3, v3).setLight(brightness);
                renderer.addVertex(matrix, x1, y1, z2).setColor(color).setUv(u4, v4).setLight(brightness);
            }
            case WEST -> {
                renderer.addVertex(matrix, x1, y1, z2).setColor(color).setUv(u1, v1).setLight(brightness);
                renderer.addVertex(matrix, x1, y2, z2).setColor(color).setUv(u2, v2).setLight(brightness);
                renderer.addVertex(matrix, x1, y2, z1).setColor(color).setUv(u3, v3).setLight(brightness);
                renderer.addVertex(matrix, x1, y1, z1).setColor(color).setUv(u4, v4).setLight(brightness);
            }
            case EAST -> {
                renderer.addVertex(matrix, x2, y1, z1).setColor(color).setUv(u1, v1).setLight(brightness);
                renderer.addVertex(matrix, x2, y2, z1).setColor(color).setUv(u2, v2).setLight(brightness);
                renderer.addVertex(matrix, x2, y2, z2).setColor(color).setUv(u3, v3).setLight(brightness);
                renderer.addVertex(matrix, x2, y1, z2).setColor(color).setUv(u4, v4).setLight(brightness);
            }
        }
    }

    /**
     * Renders a full fluid cuboid for the given data
     *
     * @param poseStack Pose stack instance
     * @param buffer    Buffer type
     * @param still     Still sprite
     * @param flowing   Flowing sprite
     * @param cube      Fluid cuboid
     * @param from      Fluid start
     * @param to        Fluid end
     * @param color     Fluid color
     * @param light     Quad lighting
     * @param isGas     If true, fluid is a gas
     */
    public static void renderCuboid(PoseStack poseStack, VertexConsumer buffer, FluidCuboid cube, TextureAtlasSprite still, TextureAtlasSprite flowing, Vector3f from, Vector3f to, int color, int light, boolean isGas) {
        Matrix4f matrix = poseStack.last().pose();
        int rotation = isGas ? 180 : 0;
        for (Direction dir : Direction.values()) {
            FluidFace face = cube.getFace(dir);
            if (face != null) {
                boolean isFlowing = face.isFlowing();
                int faceRot = (rotation + face.rotation()) % 360;
                putTexturedQuad(buffer, matrix, isFlowing ? flowing : still, from, to, dir, color, light, faceRot, isFlowing);
            }
        }
    }

    /**
     * Renders a list of fluid cuboids
     *
     * @param poseStack Pose stack instance
     * @param buffer    Buffer instance
     * @param cubes     List of cubes to render
     * @param fluid     Fluid to use in rendering
     * @param light     Light level from TER
     */
    public static void renderCuboids(PoseStack poseStack, VertexConsumer buffer, List<FluidCuboid> cubes, FluidStack fluid, int light) {
        if (fluid.isEmpty()) {
            return;
        }

        // fluid attributes, fetch once for all fluids to save effort
        FluidType fluidType = fluid.getFluid().getFluidType();
        IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(fluidType);
        TextureAtlasSprite still = getBlockSprite(attributes.getStillTexture(fluid));
        TextureAtlasSprite flowing = getBlockSprite(attributes.getFlowingTexture(fluid));
        int color = attributes.getTintColor(fluid);
        light = withBlockLight(light, fluidType.getLightLevel(fluid));
        boolean isGas = fluidType.isLighterThanAir();

        // render all given cuboids
        for (FluidCuboid cube : cubes) {
            renderCuboid(poseStack, buffer, cube, still, flowing, cube.getFromScaled(), cube.getToScaled(), color, light, isGas);
        }
    }

    /**
     * Renders a fluid cuboid with the given offset, used to manually place cuboids from a list for rendering {@link #renderCuboids(PoseStack, VertexConsumer, List, FluidStack, int)}
     *
     * @param poseStack Pose stack instance
     * @param buffer    Buffer type
     * @param cube      Fluid cuboid
     * @param yOffset   Amount to offset the cube in the Y direction, used in faucets for rendering fluid in lower block
     * @param still     Still sprite
     * @param flowing   Flowing sprite
     * @param color     Fluid color
     * @param light     Quad lighting from TER
     * @param isGas     If true, fluid is a gas
     */
    public static void renderCuboid(PoseStack poseStack, VertexConsumer buffer, FluidCuboid cube, float yOffset, TextureAtlasSprite still, TextureAtlasSprite flowing, int color, int light, boolean isGas) {
        if (yOffset != 0) {
            poseStack.pushPose();
            poseStack.translate(0, yOffset, 0);
        }
        renderCuboid(poseStack, buffer, cube, still, flowing, cube.getFromScaled(), cube.getToScaled(), color, light, isGas);
        if (yOffset != 0) {
            poseStack.popPose();
        }
    }

    /**
     * Renders a fluid cuboid with partial height based on the capacity
     *
     * @param poseStack Pose stack instance
     * @param buffer    Render type buffer instance
     * @param fluid     Fluid to render
     * @param offset    Fluid amount offset, used to animate transitions
     * @param capacity  Fluid tank capacity, must be above 0
     * @param light     Quad lighting from TER
     * @param cube      Fluid cuboid instance
     * @param flipGas   If true, flips gas cubes
     */
    public static void renderScaledCuboid(PoseStack poseStack, MultiBufferSource buffer, FluidCuboid cube, FluidStack fluid, float offset, int capacity, int light, boolean flipGas) {
        // nothing to render
        if (fluid.isEmpty() || capacity <= 0) {
            return;
        }

        // fluid attributes
        FluidType fluidType = fluid.getFluid().getFluidType();
        IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(fluidType);
        TextureAtlasSprite still = getBlockSprite(attributes.getStillTexture(fluid));
        TextureAtlasSprite flowing = getBlockSprite(attributes.getFlowingTexture(fluid));
        boolean isGas = fluidType.isLighterThanAir();
        light = withBlockLight(light, fluidType.getLightLevel(fluid));

        // determine height based on fluid amount
        Vector3f from = cube.getFromScaled();
        Vector3f to = cube.getToScaled();
        // gas renders upside down
        float minY = from.y();
        float maxY = to.y();
        float height = (fluid.getAmount() - offset) / capacity;
        if (isGas && flipGas) {
            from = new Vector3f(from);
            from.y = maxY + height * (minY - maxY);
        } else {
            to = new Vector3f(to);
            to.y = minY + height * (maxY - minY);
        }

        // draw cuboid
        renderCuboid(poseStack, buffer.getBuffer(LibRenderTypes.FLUID), cube, still, flowing, from, to, attributes.getTintColor(fluid), light, isGas);
    }
}