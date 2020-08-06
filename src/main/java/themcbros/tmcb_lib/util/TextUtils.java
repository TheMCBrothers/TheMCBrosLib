package themcbros.tmcb_lib.util;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.ForgeI18n;
import themcbros.tmcb_lib.TheMCBrosLib;
import themcbros.tmcb_lib.energy.EnergyUnit;

public class TextUtils {

    private static final String ENERGY_FORMAT = "%,d";

    public static IFormattableTextComponent translate(String prefix, String suffix, Object... params) {
        String key = String.format("%s.%s.%s", prefix, TheMCBrosLib.MOD_ID, suffix);
        return new TranslationTextComponent(key, params);
    }

    public static IFormattableTextComponent energy(int amount, EnergyUnit unit) {
        String s1 = String.format(ENERGY_FORMAT, amount);
        return translate("misc", "energy", s1, unit.getName());
    }

    public static IFormattableTextComponent energyWithMax(int amount, int max, EnergyUnit unit) {
        String s1 = String.format(ENERGY_FORMAT, amount);
        String s2 = String.format(ENERGY_FORMAT, max);
        return translate("misc", "energyWithMax", s1, s2, unit.getName());
    }

    public static IFormattableTextComponent fluidWithMax(IFluidHandler tank) {
        FluidStack fluid = tank.getFluidInTank(0);
        String s1 = String.format(ENERGY_FORMAT, fluid.getAmount());
        String s2 = String.format(ENERGY_FORMAT, tank.getTankCapacity(0));
        return translate("misc", "fluidWithMax", s1, s2);
    }

    public static IFormattableTextComponent fluidWithMax(FluidStack fluid, int max) {
        ITextComponent fluidName = fluid.getDisplayName();
        String s1 = String.format(ENERGY_FORMAT, fluid.getAmount());
        String s2 = String.format(ENERGY_FORMAT, max);
        return translate("misc", fluid.getAmount() > 0 ? "fluidWithMaxName" : "empty", fluidName, s1, s2);
    }

    public static IFormattableTextComponent fluidWithMax(String fluid, int amount, int max) {
        String s1 = String.format(ENERGY_FORMAT, amount);
        String s2 = String.format(ENERGY_FORMAT, max);
        return translate("misc", amount > 0 ? "fluidWithMaxName" : "empty", fluid, s1, s2);
    }

    public static IFormattableTextComponent fluidWithMax(int amount, int max) {
        String s1 = String.format(ENERGY_FORMAT, amount);
        String s2 = String.format(ENERGY_FORMAT, max);
        return translate("misc", amount > 0 ? "fluidWithMax" : "empty", s1, s2);
    }

    public static IFormattableTextComponent fluidAmount(int amount) {
        String s1 = String.format(ENERGY_FORMAT, amount);
        return translate("misc", "fluidAmount", s1);
    }

    public static ITextComponent fluidName(FluidStack stack) {
        if (stack.isEmpty()) return translate("misc", "empty");
        return stack.getDisplayName();
    }

    public static ITextComponent fluidName(Fluid fluid) {
        if (fluid == Fluids.EMPTY) return translate("misc", "empty");
        return fluid.getAttributes().getDisplayName(FluidStack.EMPTY);
    }

    /**
     * Checks if the given key can be translated
     * @param key   Key to check
     * @return      True if it can be translated
     */
    public static boolean canTranslate(String key) {
        return !ForgeI18n.getPattern(key).equals(key);
    }

    /**
     * @return Message for holding shift
     */
    public static ITextComponent holdShiftMessage() {
        return translate("misc", "holdShift");
    }

}
