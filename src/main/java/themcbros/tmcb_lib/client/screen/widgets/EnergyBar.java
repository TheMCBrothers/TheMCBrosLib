package themcbros.tmcb_lib.client.screen.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import themcbros.tmcb_lib.TheMCBrosLib;
import themcbros.tmcb_lib.config.Config;
import themcbros.tmcb_lib.energy.EnergyUnit;
import themcbros.tmcb_lib.energy.IEnergyProvider;
import themcbros.tmcb_lib.util.TextUtils;

public class EnergyBar extends Widget {

    public static final ResourceLocation TEXTURE = TheMCBrosLib.rl("textures/gui/energy_bar.png");

    private final IEnergyProvider energyProvider;
    private final ContainerScreen<?> screen;
    private EnergyUnit unit = Config.CLIENT_CONFIG.energyUnit;

    public EnergyBar(int xIn, int yIn, IEnergyProvider energyProvider, ContainerScreen<?> screen) {
        super(xIn, yIn, 8, 48, StringTextComponent.EMPTY);
        this.active = true;
        this.energyProvider = energyProvider;
        this.screen = screen;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            int xOff = this.unit.ordinal() * 18;
            this.blit(matrixStack, this.x - 1, this.y - 1, xOff, 0, this.width + 2, this.height + 2);
            int i = this.getScaledHeight();
            this.blit(matrixStack, this.x, this.y + this.height - i, xOff + this.width + 2, 0, this.width, i);
        }
    }

    @Override
    public void blit(MatrixStack matrixStack, int screenX, int screenY, int textureX, int textureY, int width, int height) {
        super.blit(matrixStack, screenX, screenY, textureX, textureY, width, height);
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

    private int getEnergyStored() {
        return (int) this.unit.getDisplayEnergy(this.energyProvider.getEnergyStored());
    }

    private int getMaxEnergyStored() {
        return (int) this.unit.getDisplayEnergy(this.energyProvider.getMaxEnergyStored());
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        ITextComponent energy = TextUtils.energyWithMax(this.getEnergyStored(), this.getMaxEnergyStored(), this.unit);
        this.screen.renderTooltip(matrixStack, energy, mouseX, mouseY);
    }
}
