package themcbros.tmcb_lib.energy;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public enum EnergyUnit implements IStringSerializable {

    REDSTONE_FLUX("RF", null, energy -> energy, Styles.REDSTONE_FLUX),
    FORGE_ENERGY("FE", null, energy -> energy, Styles.FORGE_ENERGY),
    USELESS_ENERGY("UE", () -> ModList.get().isLoaded("uselessmod"), energy -> energy, Styles.USELESS_ENERGY),
    ENERGY_UNITS("EU", () -> ModList.get().isLoaded("ic2"), energy -> (long) (0.25F * energy), Styles.ENERGY_UNITS),
    IMMERSIVE_FLUX("IF", () -> ModList.get().isLoaded("immersiveengineering"), energy -> energy, Styles.IMMERSIVE_FLUX),
    JOULES("J", () -> ModList.get().isLoaded("mekanism"), energy -> (long) (2.5F * energy), Styles.JOULES);

    private final String name;
    private final Style style;
    private final Function<Long, Long> multiplier;
    @Nullable
    private final Supplier<Boolean> isActive;

    EnergyUnit(String name, @Nullable Supplier<Boolean> isActive, Function<Long, Long> multiplier, Style style) {
        this.name = name;
        this.style = style;
        this.isActive = isActive;
        this.multiplier = multiplier;
    }

    public long getDisplayEnergy(long forgeEnergy) {
        return this.multiplier.apply(forgeEnergy);
    }

    public boolean isActive() {
        return this.isActive == null || this.isActive.get();
    }

    public Style getStyle() {
        return style;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getString() {
        return this.name;
    }

    private static class Styles {
        private static final Style REDSTONE_FLUX = Style.EMPTY.setColor(Color.func_240744_a_(TextFormatting.RED));
        private static final Style FORGE_ENERGY = Style.EMPTY.setColor(Color.func_240744_a_(TextFormatting.RED));
        private static final Style USELESS_ENERGY = Style.EMPTY.setColor(Color.func_240743_a_(0x62B15F));
        private static final Style ENERGY_UNITS = Style.EMPTY.setColor(Color.func_240744_a_(TextFormatting.RED));
        private static final Style IMMERSIVE_FLUX = Style.EMPTY.setColor(Color.func_240744_a_(TextFormatting.GOLD));
        private static final Style JOULES = Style.EMPTY.setColor(Color.func_240743_a_(0x3BFB98));
    }

}
