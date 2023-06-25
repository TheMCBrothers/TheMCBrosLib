package net.themcbrothers.lib.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Slot that only items can be placed which fulfil a given predicate
 */
public class PredicateSlot extends Slot {
    private final Predicate<ItemStack> predicate;

    public PredicateSlot(Container container, int slot, int x, int y, Predicate<ItemStack> predicate) {
        super(container, slot, x, y);
        this.predicate = predicate;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return this.predicate.test(stack);
    }
}
