package net.themcbrothers.lib.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraftforge.common.Tags;
import net.themcbrothers.lib.TheMCBrosLib;

import java.util.function.Consumer;

public class LibraryRecipeProvider extends VanillaRecipeProvider {
    public LibraryRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
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
