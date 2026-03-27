package net.themcbrothers.lib.util;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipePropertySet;

import java.util.HashMap;
import java.util.Map;

/**
 * Helps with recipes
 */
public final class RecipeHelper {
    private RecipeHelper() {
    }

    public static void addPropertySet(ResourceKey<RecipePropertySet> key, RecipeManager.IngredientExtractor extractor) {
        getRecipePropertySets().put(key, extractor);
    }

    private static Map<ResourceKey<RecipePropertySet>, RecipeManager.IngredientExtractor> getRecipePropertySets() {
        if (RecipeManager.RECIPE_PROPERTY_SETS instanceof HashMap<ResourceKey<RecipePropertySet>, RecipeManager.IngredientExtractor>) {
            return RecipeManager.RECIPE_PROPERTY_SETS;
        }

        RecipeManager.RECIPE_PROPERTY_SETS = Maps.newHashMap(RecipeManager.RECIPE_PROPERTY_SETS);
        return RecipeManager.RECIPE_PROPERTY_SETS;
    }
}
