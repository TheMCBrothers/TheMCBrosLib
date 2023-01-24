package net.themcbrothers.lib.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Extension of {@link Recipe} to set some methods that always set.
 *
 * @param <C>
 */
public interface CommonRecipe<C extends Container> extends Recipe<C> {
    @Override
    default ItemStack assemble(C container) {
        return getResultItem().copy();
    }

    /**
     * Deprecated means nothing outside crafting tables
     *
     * @param width  Width
     * @param height Height
     * @return True
     */
    @Deprecated
    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    /**
     * Returns true to hide the recipe from the recipe book
     *
     * @return True
     */
    @Override
    default boolean isSpecial() {
        return true;
    }
}
