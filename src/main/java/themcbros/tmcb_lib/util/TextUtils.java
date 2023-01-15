package themcbros.tmcb_lib.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import themcbros.tmcb_lib.TheMCBrosLib;
import themcbros.tmcb_lib.energy.EnergyUnit;

public class TextUtils {
    public static final Component HOLD_SHIFT_MESSAGE = translate("misc", "holdShift");

    private static final String FORMAT = "%,d";

    public static MutableComponent translate(String prefix, String suffix, Object... params) {
        String key = String.format("%s.%s.%s", prefix, TheMCBrosLib.MOD_ID, suffix);
        return new TranslatableComponent(key, params);
    }

    public static MutableComponent energy(int amount, EnergyUnit unit) {
        String s1 = String.format(FORMAT, amount);
        return translate("misc", "energy", s1, unit.getName());
    }

    public static MutableComponent energyWithMax(int amount, int max, EnergyUnit unit) {
        String s1 = String.format(FORMAT, amount);
        String s2 = String.format(FORMAT, max);
        return translate("misc", "energyWithMax", s1, s2, unit.getName());
    }

    public static MutableComponent fluidWithMax(IFluidHandler tank) {
        FluidStack fluid = tank.getFluidInTank(0);
        String s1 = String.format(FORMAT, fluid.getAmount());
        String s2 = String.format(FORMAT, tank.getTankCapacity(0));
        return translate("misc", "fluidWithMax", s1, s2);
    }

    public static MutableComponent fluidWithMax(FluidStack fluid, int max) {
        Component fluidName = fluid.getDisplayName();
        String s1 = String.format(FORMAT, fluid.getAmount());
        String s2 = String.format(FORMAT, max);
        return translate("misc", fluid.getAmount() > 0 ? "fluidWithMaxName" : "empty", fluidName, s1, s2);
    }

    public static MutableComponent fluidWithMax(String fluid, int amount, int max) {
        String s1 = String.format(FORMAT, amount);
        String s2 = String.format(FORMAT, max);
        return translate("misc", amount > 0 ? "fluidWithMaxName" : "empty", fluid, s1, s2);
    }

    public static MutableComponent fluidWithMax(int amount, int max) {
        String s1 = String.format(FORMAT, amount);
        String s2 = String.format(FORMAT, max);
        return translate("misc", amount > 0 ? "fluidWithMax" : "empty", s1, s2);
    }

    public static MutableComponent fluidAmount(int amount) {
        String s1 = String.format(FORMAT, amount);
        return translate("misc", "fluidAmount", s1);
    }

    public static Component fluidName(FluidStack stack) {
        if (stack.isEmpty()) return translate("misc", "empty");
        return stack.getDisplayName();
    }

    public static Component fluidName(Fluid fluid) {
        if (fluid == Fluids.EMPTY) return translate("misc", "empty");
        return fluid.getAttributes().getDisplayName(FluidStack.EMPTY);
    }

    /**
     * Checks if the given key can be translated
     *
     * @param key Key to check
     * @return True if it can be translated
     */
    public static boolean canTranslate(String key) {
        return !ForgeI18n.getPattern(key).equals(key);
    }
}
