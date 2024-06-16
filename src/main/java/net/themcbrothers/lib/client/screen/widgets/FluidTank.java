package net.themcbrothers.lib.client.screen.widgets;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.themcbrothers.lib.client.render.LibRenderTypes;
import net.themcbrothers.lib.util.TooltipHelper;

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
        super(x, y, width, height, Component.empty());
        this.fluidHandler = fluidHandler;
        this.screen = screen;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.drawFluid(guiGraphics, this.getX(), this.getY(), this.getFluid());
    }

    public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<Component> tooltip = Lists.newArrayList();
        tooltip.add(TEXT_UTILS.fluidName(this.getFluid()));
        TooltipHelper.appendAmount(tooltip, this.getFluid().getAmount(), this.getCapacity(), "mB", ChatFormatting.GRAY);

        // Only append Registry Name and Mod Name when the tank is not empty
        if (!this.getFluid().isEmpty()) {
            TooltipHelper.appendRegistryName(tooltip, BuiltInRegistries.FLUID, this.getFluid().getFluid(), ChatFormatting.DARK_GRAY);
            TooltipHelper.appendModNameFromFluid(tooltip, this.getFluid());
        }

        guiGraphics.renderTooltip(this.screen.getMinecraft().font, Lists.transform(tooltip, Component::getVisualOrderText), mouseX, mouseY);
    }

    public FluidStack getFluid() {
        return this.fluidHandler.getFluidInTank(0);
    }

    private int getCapacity() {
        return this.fluidHandler.getTankCapacity(0);
    }

    // Rendering methods

    protected void drawFluid(GuiGraphics guiGraphics, final int xPosition, final int yPosition, @Nullable FluidStack fluidStack) {
        if (fluidStack == null || fluidStack.isEmpty()) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);
        int fluidColor = getColorTint(fluidStack);

        int amount = fluidStack.getAmount();
        int scaledAmount = (amount * height) / getCapacity();
        if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }

        drawTiledSprite(guiGraphics, xPosition, yPosition, width, height, fluidColor, scaledAmount, fluidStillSprite);
    }

    protected void drawTiledSprite(GuiGraphics guiGraphics, final int xPosition, final int yPosition, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
        final VertexConsumer buffer = guiGraphics.bufferSource().getBuffer(LibRenderTypes.FLUID_GUI);

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

                    drawTextureWithMasking(buffer, x, y, sprite, maskTop, maskRight, color);
                }
            }
        }
    }

    private static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation fluidStill = renderProperties.getStillTexture(fluidStack);
        return minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidStill);
    }

    private static int getColorTint(FluidStack fluidStack) {
        return IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
    }

    private static void drawTextureWithMasking(VertexConsumer vertexConsumer, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, int color) {
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();
        uMax = uMax - (maskRight / 16F * (uMax - uMin));
        vMax = vMax - (maskTop / 16F * (vMax - vMin));

        int zLevel = 100;

        vertexConsumer.addVertex(xCoord, yCoord + 16, zLevel).setUv(uMin, vMax).setColor(color);
        vertexConsumer.addVertex(xCoord + 16 - maskRight, yCoord + 16, zLevel).setUv(uMax, vMax).setColor(color);
        vertexConsumer.addVertex(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).setUv(uMax, vMin).setColor(color);
        vertexConsumer.addVertex(xCoord, yCoord + maskTop, zLevel).setUv(uMin, vMin).setColor(color);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(false);
    }
}
