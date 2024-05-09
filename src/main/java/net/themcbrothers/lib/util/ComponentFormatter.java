package net.themcbrothers.lib.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.themcbrothers.lib.energy.EnergyUnit;

/**
 * Helps with {@link Component}s
 */
public class ComponentFormatter {
    private static final String FORMAT = "%,d";

    private final String modId;

    public ComponentFormatter(String modId) {
        this.modId = modId;
    }

    public MutableComponent translate(String prefix, String suffix, Object... params) {
        String key = String.format("%s.%s.%s", prefix, this.modId, suffix);
        return Component.translatable(key, params);
    }

    /**
     * @param amount Amount as Forge Energy
     * @param unit   Energy Unit
     * @return Formatted Energy (eg. 4,000 FE)
     */
    public MutableComponent energy(long amount, EnergyUnit unit) {
        String s1 = String.format(FORMAT, unit.getDisplayEnergy(amount));
        return translate("misc", "energy", s1, unit.getName());
    }

    /**
     * @param amount Amount as Forge Energy
     * @param max    Capacity amount as Forge Energy
     * @param unit   Energy Unit
     * @return Formatted Energy (eg. 4,000 FE)
     */
    public MutableComponent energyWithMax(long amount, long max, EnergyUnit unit) {
        String s1 = String.format(FORMAT, unit.getDisplayEnergy(amount));
        String s2 = String.format(FORMAT, unit.getDisplayEnergy(max));
        return translate("misc", "energyWithMax", s1, s2, unit.getName());
    }

    public MutableComponent fluidWithMax(IFluidHandler tank) {
        FluidStack fluid = tank.getFluidInTank(0);
        String s1 = String.format(FORMAT, fluid.getAmount());
        String s2 = String.format(FORMAT, tank.getTankCapacity(0));
        return translate("misc", "fluidWithMax", s1, s2);
    }

    public MutableComponent fluidWithMax(FluidStack fluid, int max) {
        Component fluidName = fluid.getHoverName();
        String s1 = String.format(FORMAT, fluid.getAmount());
        String s2 = String.format(FORMAT, max);
        return translate("misc", fluid.getAmount() > 0 ? "fluidWithMaxName" : "empty", fluidName, s1, s2);
    }

    public MutableComponent fluidWithMax(String fluid, int amount, int max) {
        String s1 = String.format(FORMAT, amount);
        String s2 = String.format(FORMAT, max);
        return translate("misc", amount > 0 ? "fluidWithMaxName" : "empty", fluid, s1, s2);
    }

    public MutableComponent fluidWithMax(int amount, int max) {
        String s1 = String.format(FORMAT, amount);
        String s2 = String.format(FORMAT, max);
        return translate("misc", amount > 0 ? "fluidWithMax" : "empty", s1, s2);
    }

    public MutableComponent fluidAmount(int amount) {
        String s1 = String.format(FORMAT, amount);
        return translate("misc", "fluidAmount", s1);
    }

    public Component fluidName(FluidStack stack) {
        if (stack.isEmpty()) return translate("misc", "empty");
        return stack.getHoverName();
    }

    public Component fluidName(Fluid fluid) {
        if (fluid == Fluids.EMPTY) return translate("misc", "empty");
        return fluid.getFluidType().getDescription(FluidStack.EMPTY);
    }
}
