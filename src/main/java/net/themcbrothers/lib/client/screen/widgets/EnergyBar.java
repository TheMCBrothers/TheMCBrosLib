package net.themcbrothers.lib.client.screen.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.themcbrothers.lib.TheMCBrosLib;
import net.themcbrothers.lib.config.Config;
import net.themcbrothers.lib.energy.EnergyProvider;
import net.themcbrothers.lib.energy.EnergyUnit;

import java.util.List;

import static net.themcbrothers.lib.TheMCBrosLib.TEXT_UTILS;

/**
 * Widget for displaying energy from a {@link EnergyProvider}
 */
public class EnergyBar extends AbstractWidget {
    public static final Identifier TEXTURE = TheMCBrosLib.id("textures/gui/energy_bar.png");
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 256;

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
        int xOff = this.unit.ordinal() * (this.width * 2 + 2);
        int yOff = this.size.getYOff();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX() - 1, this.getY() - 1, xOff, yOff, this.width + 2, this.height + 2, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        int i = this.getScaledHeight();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY() + this.height - i, xOff + this.width + 2, yOff, this.width, i, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component energy = TEXT_UTILS.energyWithMax(this.energyProvider.getEnergyStored(), this.energyProvider.getMaxEnergyStored(), this.unit);
        ClientTooltipComponent component = ClientTooltipComponent.create(energy.getVisualOrderText());
        guiGraphics.renderTooltip(this.screen.getFont(), List.of(component), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
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
        double i = (double) this.energyProvider.getEnergyStored();
        double j = (double) this.energyProvider.getMaxEnergyStored();
        double h = this.height;
        return i != 0 && j != 0 ? (int) Math.clamp(i / j * h, 1, h) : 0;
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
