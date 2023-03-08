package net.themcbrothers.lib.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.themcbrothers.lib.config.Config;

import java.util.List;

/**
 * Helps with tooltips, this should only be used on the client side
 */
public class TooltipHelper {
    /**
     * Appends the given mod name to the tooltip
     *
     * @param tooltip Tooltip
     * @param modName Mod Name
     */
    public static void appendModName(List<Component> tooltip, String modName) {
        String format = Config.CLIENT_CONFIG.getModNameFormat();

        // If mod name is blank or no format is set, skip
        if (modName == null || modName.isEmpty() || format.isEmpty()) {
            return;
        }

        // Only append mod name if it isn't already there
        if (!isModNamePresent(tooltip, modName)) {
            tooltip.add(new TextComponent(format + modName));
        }
    }

    /**
     * Appends the Mod Name of the given Fluid Stack to the tooltip
     *
     * @param tooltip    Tooltip
     * @param fluidStack Fluid Stack
     */
    public static void appendModNameFromFluid(List<Component> tooltip, FluidStack fluidStack) {
        ModHelper.getCreatorModId(fluidStack)
                .map(ModHelper::getModName)
                .ifPresent(modId -> appendModName(tooltip, modId));
    }

    /**
     * Appends the Mod Name of the given Item Stack to the tooltip
     *
     * @param tooltip   Tooltip
     * @param itemStack Item Stack
     */
    public static void appendModNameFromItem(List<Component> tooltip, ItemStack itemStack) {
        ModHelper.getCreatorModId(itemStack)
                .map(ModHelper::getModName)
                .ifPresent(modName -> appendModName(tooltip, modName));
    }

    /**
     * Checks if the mod name is already present (at the end) on the given tooltip
     *
     * @param tooltip Tooltip
     * @param modName Mod Name
     * @return TRUE if the tooltip already contains the mod name at the end, otherwise FALSE
     */
    public static boolean isModNamePresent(List<Component> tooltip, String modName) {
        if (tooltip.size() > 1) {
            Component line = tooltip.get(tooltip.size() - 1);
            String withoutFormatting = ChatFormatting.stripFormatting(line.getString());
            return modName.equals(withoutFormatting);
        }

        return false;
    }
}
