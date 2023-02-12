package net.themcbrothers.lib.client.screen.widgets;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

import static net.themcbrothers.lib.TheMCBrosLib.TEXT_UTILS;

/**
 * Widget for displaying a fluid from a {@link IFluidHandler}
 */
public class FluidTank extends AbstractWidget {

    private static final int TEX_WIDTH = 16;
    private static final int TEX_HEIGHT = 16;
    private static final int MIN_FLUID_HEIGHT = 1;

    private final IFluidHandler fluidHandler;
    private final AbstractContainerScreen<?> screen;

    public FluidTank(int x, int y, int width, int height, IFluidHandler fluidHandler, AbstractContainerScreen<?> screen) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.fluidHandler = fluidHandler;
        this.screen = screen;
    }

    @Override
    public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.drawFluid(this.x, this.y, this.getFluid());
    }

    @Override
    public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
        List<Component> list = List.of(TEXT_UTILS.fluidName(this.getFluid()), TEXT_UTILS.fluidWithMax(this.fluidHandler));
        this.screen.renderTooltip(matrixStack, Lists.transform(list, Component::getVisualOrderText), mouseX, mouseY);
    }

    public FluidStack getFluid() {
        return this.fluidHandler.getFluidInTank(0);
    }

    private int getCapacity() {
        return this.fluidHandler.getTankCapacity(0);
    }

    // Rendering methods

    protected void drawFluid(final int xPosition, final int yPosition, @Nullable FluidStack fluidStack) {
        if (fluidStack == null || fluidStack.isEmpty()) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);

        FluidAttributes attributes = fluidStack.getFluid().getAttributes();
        int fluidColor = attributes.getColor(fluidStack);

        int amount = fluidStack.getAmount();
        int scaledAmount = (amount * height) / getCapacity();
        if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }

        drawTiledSprite(xPosition, yPosition, width, height, fluidColor, scaledAmount, fluidStillSprite);
    }

    protected void drawTiledSprite(final int xPosition, final int yPosition, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        setGLColorFromInt(color);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

        final int xTileCount = tiledWidth / TEX_WIDTH;
        final int xRemainder = tiledWidth - (xTileCount * TEX_WIDTH);
        final int yTileCount = scaledAmount / TEX_HEIGHT;
        final int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);

        final int yStart = yPosition + tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
                int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
                int x = xPosition + (xTile * TEX_WIDTH);
                int y = yStart - ((yTile + 1) * TEX_HEIGHT);
                if (width > 0 && height > 0) {
                    int maskTop = TEX_HEIGHT - height;
                    int maskRight = TEX_WIDTH - width;

                    drawTextureWithMasking(x, y, sprite, maskTop, maskRight, 100);
                }
            }
        }
    }

    private static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Fluid fluid = fluidStack.getFluid();
        FluidAttributes attributes = fluid.getAttributes();
        ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
        return minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidStill);
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;

        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    private static void drawTextureWithMasking(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
        double uMin = textureSprite.getU0();
        double uMax = textureSprite.getU1();
        double vMin = textureSprite.getV0();
        double vMax = textureSprite.getV1();
        uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
        vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(xCoord, yCoord + 16, zLevel).uv((float) uMin, (float) vMax).endVertex();
        bufferBuilder.vertex(xCoord + 16 - maskRight, yCoord + 16, zLevel).uv((float) uMax, (float) vMax).endVertex();
        bufferBuilder.vertex(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv((float) uMax, (float) vMin).endVertex();
        bufferBuilder.vertex(xCoord, yCoord + maskTop, zLevel).uv((float) uMin, (float) vMin).endVertex();

        tesselator.end();
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean changeFocus(boolean focus) {
        return false;
    }
}
