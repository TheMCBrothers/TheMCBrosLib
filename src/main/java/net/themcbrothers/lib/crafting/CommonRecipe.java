package net.themcbrothers.lib.crafting;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Extension of {@link Recipe} to set some methods that always set.
 *
 * @param <C>
 */
public interface CommonRecipe<C extends RecipeInput> extends Recipe<C> {
    @Override
    default boolean isSpecial() {
        return true;
    }
}
