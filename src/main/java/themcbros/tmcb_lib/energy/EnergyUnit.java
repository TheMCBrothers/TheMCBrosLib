package themcbros.tmcb_lib.energy;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public enum EnergyUnit implements IStringSerializable {

    REDSTONE_FLUX("RF", null, energy -> energy, TextFormatting.RED),
    FORGE_ENERGY("FE", null, energy -> energy, TextFormatting.RED),
    USELESS_ENERGY("UE", () -> ModList.get().isLoaded("uselessmod"), energy -> energy, TextFormatting.GREEN),
    ENERGY_UNITS("EU", () -> ModList.get().isLoaded("ic2"), energy -> (long) (0.25F * energy), TextFormatting.RED),
    IMMERSIVE_FLUX("IF", () -> ModList.get().isLoaded("immersiveengineering"), energy -> energy, TextFormatting.GOLD),
    JOULES("J", () -> ModList.get().isLoaded("mekanism"), energy -> (long) (2.5F * energy), TextFormatting.GREEN);

    private final String name;
    private final TextFormatting[] formattings;
    private final Function<Long, Long> multiplier;
    @Nullable
    private final Supplier<Boolean> isActive;

    EnergyUnit(String name, @Nullable Supplier<Boolean> isActive, Function<Long, Long> multiplier, TextFormatting... formattings) {
        this.name = name;
        this.formattings = formattings;
        this.isActive = isActive;
        this.multiplier = multiplier;
    }

    public long getDisplayEnergy(long forgeEnergy) {
        return this.multiplier.apply(forgeEnergy);
    }

    public boolean isActive() {
        return this.isActive == null || this.isActive.get();
    }

    public TextFormatting[] getFormattings() {
        return this.formattings;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
