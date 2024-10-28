package net.themcbrothers.lib.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Helps with recipes
 */
public final class RecipeHelper {
    private RecipeHelper() {
    }

    /**
     * Gets the result item of a recipe using the client level's registry access
     *
     * @param recipe Any recipe
     * @return Recipe result item
     */
    public static ItemStack getResultItem(Recipe<?> recipe) {
        // TODO: 1.21.X
        return ItemStack.EMPTY;
    }
}
