package net.themcbrothers.lib.crafting;

import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidRecipeInput extends RecipeInput {
    FluidStack getFluid(int index);

    int fluidSize();

    int itemSize();

    @Override
    default int size() {
        return itemSize();
    }

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < this.fluidSize(); i++) {
            if (!this.getFluid(i).isEmpty()) {
                return false;
            }
        }

        return RecipeInput.super.isEmpty();
    }
}
