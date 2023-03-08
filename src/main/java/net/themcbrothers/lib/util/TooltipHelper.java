package net.themcbrothers.lib.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.themcbrothers.lib.config.Config;

import java.util.List;

/**
 * Helps with tooltips, this should only be used on the client side
 */
public class TooltipHelper {
    /**
     * Appends an amount and optional capacity to the tooltip
     *
     * @param tooltip    Tooltip
     * @param amount     Amount of something
     * @param capacity   Capacity, everything below 1 will show no Capacity text
     * @param unit       Unit, can be null
     * @param formatting Optional formatting
     */
    public static void appendAmount(List<Component> tooltip, int amount, int capacity, String unit, ChatFormatting... formatting) {
        final String format = "%,d";
        final MutableComponent component = Component.literal(String.format(format, amount));

        if (capacity > 0) {
            component.append(" / " + String.format(format, capacity));
        }

        if (unit != null) {
            component.append(" " + unit);
        }

        tooltip.add(component.withStyle(formatting));
    }

    /**
     * Appends the registry name of the given entry to the tooltip.
     * Note: it appends only if advanced tooltips are enabled
     *
     * @param tooltip    Tooltip
     * @param entry      Registry Entry
     * @param formatting Optional formatting
     */
    public static <T> void appendRegistryName(List<Component> tooltip, ResourceKey<? extends Registry<T>> registry, T entry, ChatFormatting... formatting) {
        if (Minecraft.getInstance().options.advancedItemTooltips) {
            ModHelper.registryNameOf(registry, entry)
                    .map(ResourceLocation::toString)
                    .map(Component::literal)
                    .map(component -> component.withStyle(formatting))
                    .ifPresent(tooltip::add);
        }
    }

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
            tooltip.add(Component.literal(format + modName));
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
