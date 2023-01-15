package net.themcbrothers.lib.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.themcbrothers.lib.energy.EnergyUnit;

public class ClientConfig {

    private final ForgeConfigSpec.EnumValue<EnergyUnit> enumEnergyUnit;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Welcome to the client config file!");

        builder.push("gui");
        this.enumEnergyUnit = builder.comment("Energy Unit rendered in machines").defineEnum("energyUnit", EnergyUnit.FORGE_ENERGY);
        builder.pop();
    }

    public EnergyUnit energyUnit = EnergyUnit.FORGE_ENERGY;

    public void bake() {
        this.energyUnit = this.enumEnergyUnit.get();
    }

    public void setEnergyUnit(EnergyUnit energyUnit) {
        this.energyUnit = energyUnit;
        this.enumEnergyUnit.set(energyUnit);
        Config.CLIENT_SPEC.save();
    }

}
