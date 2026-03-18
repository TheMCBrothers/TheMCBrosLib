package net.themcbrothers.lib.client.screen.widgets;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.ClientTooltipFlag;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.themcbrothers.lib.util.TooltipHelper;

import javax.annotation.Nullable;
import java.util.List;

import static net.themcbrothers.lib.client.render.FluidRenderer.getStillFluidSprite;

/**
 * Widget for displaying a fluid from a {@link ResourceHandler<FluidResource>}
 */
public class FluidTank extends AbstractWidget {
    private static final int MIN_FLUID_HEIGHT = 1;

    private final int handlerIndex;
    private final ResourceHandler<FluidResource> fluidHandler;
    private final AbstractContainerScreen<?> screen;

    public FluidTank(int x, int y, int width, int height, ResourceHandler<FluidResource> fluidHandler, int handlerIndex, AbstractContainerScreen<?> screen) {
        super(x, y, width, height, Component.empty());
        this.fluidHandler = fluidHandler;
        this.screen = screen;
        this.handlerIndex = handlerIndex;
    }

    public FluidTank(int x, int y, int width, int height, ResourceHandler<FluidResource> fluidHandler, AbstractContainerScreen<?> screen) {
        this(x, y, width, height, fluidHandler, 0, screen);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.drawFluid(guiGraphics, this.getX(), this.getY(), this.fluidHandler, this.handlerIndex);
    }

    public void renderToolTip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        List<Component> tooltip = this.getFluid().getTooltipLines(Item.TooltipContext.of(minecraft.level), null, ClientTooltipFlag.of(minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL));
        TooltipHelper.appendAmount(tooltip, this.getFluid().getAmount(), this.getCapacity(), "mB", ChatFormatting.GRAY);

        // Only append Registry Name and Mod Name when the tank is not empty
        if (!this.getFluid().isEmpty()) {
            TooltipHelper.appendRegistryName(tooltip, BuiltInRegistries.FLUID, this.getFluid().getFluid(), ChatFormatting.DARK_GRAY);
            TooltipHelper.appendModNameFromFluid(tooltip, this.getFluid());
        }

        guiGraphics.tooltip(this.screen.getMinecraft().font, Lists.transform(tooltip, component -> ClientTooltipComponent.create(component.getVisualOrderText())), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
    }

    public FluidStack getFluid() {
        return FluidUtil.getStack(this.fluidHandler, this.handlerIndex);
    }

    private int getCapacity() {
        return this.fluidHandler.getCapacityAsInt(this.handlerIndex, FluidResource.EMPTY);
    }

    // Rendering methods

    protected void drawFluid(GuiGraphicsExtractor guiGraphics, int xPosition, int yPosition, ResourceHandler<FluidResource> resourceHandler, int index) {
        FluidResource resource = resourceHandler.getResource(index);
        this.drawFluid(guiGraphics, xPosition, yPosition, resource, resourceHandler.getAmountAsLong(index), resourceHandler.getCapacityAsLong(index, resource));
    }

    protected void drawFluid(GuiGraphicsExtractor guiGraphics, int xPosition, int yPosition, @Nullable FluidResource resource, long amount, long capacity) {
        if (resource == null || resource.isEmpty()) {
            return;
        }

        FluidStack fluidStack = resource.toStack((int) amount);
        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);
        int fluidColor = getColorTint(fluidStack);

        if (amount > 0) {
            long longScaledAmount = (amount * height) / capacity;
            int scaledAmount = Math.clamp(longScaledAmount, MIN_FLUID_HEIGHT, height);
            drawTiledSprite(guiGraphics, width, height, fluidColor, scaledAmount, fluidStillSprite, xPosition, yPosition);
        }
    }

    private static void drawTiledSprite(GuiGraphicsExtractor guiGraphics, int tiledWidth, int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite, int xPosition, int yPosition) {
        SpriteContents spriteContents = sprite.contents();
        GuiSpriteScaling.Tile tileScaling = new GuiSpriteScaling.Tile(spriteContents.width(), spriteContents.height());

        yPosition = yPosition + tiledHeight - scaledAmount;

        guiGraphics.enableScissor(xPosition, yPosition, xPosition + tiledWidth, yPosition + scaledAmount);
        {
            guiGraphics.blitTiledSprite(
                    RenderPipelines.GUI_TEXTURED,
                    sprite,
                    xPosition,
                    yPosition,
                    tiledWidth,
                    scaledAmount,
                    0,
                    0,
                    tileScaling.width(),
                    tileScaling.height(),
                    tileScaling.width(),
                    tileScaling.height(),
                    color
            );
        }
        guiGraphics.disableScissor();
    }

    private static int getColorTint(FluidStack fluidStack) {
        FluidTintSource tintSource = Minecraft.getInstance()
                .getModelManager()
                .getFluidStateModelSet()
                .get(fluidStack.getFluid().defaultFluidState())
                .fluidTintSource();

        if (tintSource == null) {
            return -1;
        }

        return tintSource.colorAsStack(fluidStack);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(false);
    }
}
