package net.themcbrothers.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.common.Tags;
import net.themcbrothers.lib.TheMCBrosLib;

import java.util.concurrent.CompletableFuture;

public class LibraryRecipeProvider extends RecipeProvider {
    private LibraryRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput p_360872_) {
        super(lookupProvider, p_360872_);
    }

    @Override
    protected void buildRecipes() {
        this.shaped(RecipeCategory.TOOLS, TheMCBrosLib.WRENCH)
                .pattern("X X")
                .pattern(" X ")
                .pattern(" X ")
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_wrench", has(TheMCBrosLib.WRENCH))
                .save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner {
        protected Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new LibraryRecipeProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "Library recipes";
        }
    }
}
