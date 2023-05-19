package net.themcbrothers.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
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
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null) {
            throw new NullPointerException("level must not be null.");
        }

        RegistryAccess registryAccess = level.registryAccess();
        return recipe.getResultItem(registryAccess);
    }
}
