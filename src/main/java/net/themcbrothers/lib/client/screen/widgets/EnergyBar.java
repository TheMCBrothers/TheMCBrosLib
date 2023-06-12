package net.themcbrothers.lib.client.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.themcbrothers.lib.TheMCBrosLib;
import net.themcbrothers.lib.config.Config;
import net.themcbrothers.lib.energy.EnergyProvider;
import net.themcbrothers.lib.energy.EnergyUnit;

import static net.themcbrothers.lib.TheMCBrosLib.TEXT_UTILS;

/**
 * Widget for displaying energy from a {@link EnergyProvider}
 */
public class EnergyBar extends AbstractWidget {
    public static final ResourceLocation TEXTURE = TheMCBrosLib.rl("textures/gui/energy_bar.png");

    private final EnergyProvider energyProvider;
    private final AbstractContainerScreen<?> screen;
    private final Size size;
    private EnergyUnit unit = Config.CLIENT_CONFIG.getEnergyUnit();

    public EnergyBar(int xIn, int yIn, Size size, EnergyProvider energyProvider, AbstractContainerScreen<?> screen) {
        super(xIn, yIn, size.width, size.height, Component.empty());
        this.active = true;
        this.energyProvider = energyProvider;
        this.screen = screen;
        this.size = size;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int xOff = this.unit.ordinal() * (this.width * 2 + 2);
        int yOff = this.size.getYOff();
        guiGraphics.blit(TEXTURE, this.getX() - 1, this.getY() - 1, xOff, yOff, this.width + 2, this.height + 2);
        int i = this.getScaledHeight();
        guiGraphics.blit(TEXTURE, this.getX(), this.getY() + this.height - i, xOff + this.width + 2, yOff, this.width, i);
    }

    public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component energy = TEXT_UTILS.energyWithMax(this.energyProvider.getEnergyStored(), this.energyProvider.getMaxEnergyStored(), this.unit);
        guiGraphics.renderTooltip(this.screen.getMinecraft().font, energy, mouseX, mouseY);
    }

    @Override
    public void onClick(double posX, double posY) {
        this.cycleUnit();
    }

    private void cycleUnit() {
        int i = this.unit.ordinal() + 1;
        if (i >= EnergyUnit.values().length) i = 0;
        this.unit = EnergyUnit.values()[i];
        if (!this.unit.isActive()) {
            this.cycleUnit();
        } else {
            Config.CLIENT_CONFIG.setEnergyUnit(this.unit);
        }
    }

    private int getScaledHeight() {
        float i = (float) this.energyProvider.getEnergyStored();
        float j = (float) this.energyProvider.getMaxEnergyStored();
        float h = (float) this.height;
        return i != 0 && j != 0 ? (int) (i / j * h) : 0;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(false);
    }

    public enum Size {
        _8x48(8, 48),
        _10x50(10, 50);

        private final int width, height;

        Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        private int getYOff() {
            int off = 0;

            for (int i = ordinal() - 1; i >= 0; i--) {
                off += values()[i].height + 2;
            }

            return off;
        }
    }
}
