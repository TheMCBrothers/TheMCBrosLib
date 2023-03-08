package net.themcbrothers.lib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Helps with other mods
 */
public final class ModHelper {
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

        return Optional.ofNullable(fluidStack.getFluid().getRegistryName())
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
