package net.themcbrothers.lib.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Extension of {@link Recipe} to set some methods that always set.
 *
 * @param <C>
 */
public interface CommonRecipe<C extends RecipeInput> extends Recipe<C> {
    @Override
    default ItemStack assemble(C container, HolderLookup.Provider provider) {
        return getResultItem(provider).copy();
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
