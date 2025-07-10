package net.themcbrothers.lib.crafting;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class FluidRecipeWrapper implements FluidRecipeInput {
    protected final IItemHandler items;
    protected final IFluidHandler fluids;

    public FluidRecipeWrapper(IItemHandler items, IFluidHandler fluids) {
        this.items = items;
        this.fluids = fluids;
    }

    @Override
    public FluidStack getFluid(int index) {
        return this.fluids.getFluidInTank(index);
    }

    @Override
    public int fluidSize() {
        return this.fluids.getTanks();
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.getStackInSlot(index);
    }

    @Override
    public int itemSize() {
        return this.items.getSlots();
    }
}
