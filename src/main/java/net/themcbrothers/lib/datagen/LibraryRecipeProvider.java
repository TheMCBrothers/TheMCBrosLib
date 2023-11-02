package net.themcbrothers.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.neoforged.neoforge.common.Tags;
import net.themcbrothers.lib.TheMCBrosLib;

import java.util.concurrent.CompletableFuture;

public class LibraryRecipeProvider extends VanillaRecipeProvider {
    public LibraryRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeConsumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TheMCBrosLib.WRENCH)
                .pattern("X X")
                .pattern(" X ")
                .pattern(" X ")
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_wrench", has(TheMCBrosLib.WRENCH))
                .save(recipeConsumer);
    }
}
