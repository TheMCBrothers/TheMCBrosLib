package net.themcbrothers.lib.inventory;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.themcbrothers.lib.TheMCBrosLib;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Slot for Energy Item Stacks
 *
 * @since 4.2.0
 */
public class EnergySlot extends Slot {
    public static final Identifier EMPTY_SLOT_ENERGY = TheMCBrosLib.id("item/empty_slot_energy");

    private final ItemMode itemMode;
    private final boolean showIcon;

    /**
     * Constructor for advanced use of this slot class
     *
     * @param container Container
     * @param slot      Slot index
     * @param x         X position
     * @param y         Y position
     * @param itemMode  {@link ItemMode}
     * @param showIcon  Shall the icon be visible?
     */
    public EnergySlot(Container container, int slot, int x, int y, ItemMode itemMode, boolean showIcon) {
        super(container, slot, x, y);
        this.itemMode = itemMode;
        this.showIcon = showIcon;
    }

    /**
     * Default constructor for a slot that accepts items that provide energy to a machine
     *
     * @param container Container
     * @param slot      Slot index
     * @param x         X position
     * @param y         Y position
     */
    public EnergySlot(Container container, int slot, int x, int y) {
        this(container, slot, x, y, ItemMode.EXTRACT, true);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return isValid(stack, this.itemMode);
    }

    @Nullable
    @Override
    public Identifier getNoItemIcon() {
        return this.showIcon ? EMPTY_SLOT_ENERGY : super.getNoItemIcon();
    }

    /**
     * Check if an item stack is valid for an energy slot
     *
     * @param stack    Item Stack input
     * @param itemMode {@link ItemMode}
     * @return {@code true} if the given {@link ItemStack} valid for an {@link EnergySlot}, otherwise {@code false}
     */
    public static boolean isValid(ItemStack stack, ItemMode itemMode) {
        return true; // TODO: 26.1
//        return Optional.ofNullable(stack.getCapability(Capabilities.Energy.ITEM)).map(energyStorage -> switch (itemMode) {
//            case EXTRACT -> energyStorage.canExtract();
//            case RECEIVE -> energyStorage.canReceive();
//            case EXTRACT_AND_RECEIVE -> energyStorage.canExtract() && energyStorage.canReceive();
//            case EXTRACT_OR_RECEIVE -> energyStorage.canExtract() || energyStorage.canReceive();
//        }).orElse(false);
    }

    /**
     * Item Mode for an Item Stack
     */
    public enum ItemMode {
        /**
         * Item Stack must be able to extract energy
         */
        EXTRACT,
        /**
         * Item Stack must be able to receive energy
         */
        RECEIVE,
        /**
         * Item Stack must be able to extract AND receive energy
         */
        EXTRACT_AND_RECEIVE,
        /**
         * Item Stack must be able to extract OR receive energy
         */
        EXTRACT_OR_RECEIVE
    }
}
