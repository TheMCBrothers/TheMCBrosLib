package net.themcbrothers.lib.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Helps with other mods
 */
public final class ModHelper {
    private ModHelper() {
    }

    /**
     * Returns an optional resource location
     *
     * @param entry Registry Entry
     * @return Resource Location as Optional
     */
    public static <T> Optional<ResourceLocation> registryNameOf(Registry<T> registry, T entry) {
        return Optional.ofNullable(registry.getKey(entry));
    }

    /**
     * Gets the Mod ID for the given fluid
     *
     * @param fluidStack Fluid Stack
     * @return Creator Mod ID
     */
    public static Optional<String> getCreatorModId(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return Optional.empty();
        }

        return registryNameOf(BuiltInRegistries.FLUID, fluidStack.getFluid())
                .map(ResourceLocation::getNamespace);
    }

    /**
     * Gets the Mod ID for the given item
     *
     * @param itemStack Item Stack
     * @return Creator Mod ID
     */
    public static Optional<String> getCreatorModId(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Optional.empty();
        }

        String modId = itemStack.getItem().getCreatorModId(itemStack);
        return Optional.ofNullable(modId);
    }

    /**
     * Gets the name of a mod
     *
     * @param modId Mod ID
     * @return Name of the Mod or the capitalized Mod ID
     */
    public static String getModName(String modId) {
        return ModList.get()
                .getModContainerById(modId)
                .map(ModContainer::getModInfo)
                .map(IModInfo::getDisplayName)
                .orElse(StringUtils.capitalize(modId));
    }
}
