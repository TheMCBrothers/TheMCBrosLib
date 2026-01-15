package net.themcbrothers.lib.crafting;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

public class FluidRecipeWrapper implements FluidRecipeInput {
    protected final ResourceHandler<ItemResource> items;
    protected final ResourceHandler<FluidResource> fluids;

    public FluidRecipeWrapper(ResourceHandler<ItemResource> items, ResourceHandler<FluidResource> fluids) {
        this.items = items;
        this.fluids = fluids;
    }

    @Override
    public FluidStack getFluid(int index) {
        return FluidUtil.getStack(this.fluids, index);
    }

    @Override
    public int fluidSize() {
        return this.fluids.size();
    }

    @Override
    public ItemStack getItem(int index) {
        return ItemUtil.getStack(this.items, index);
    }

    @Override
    public int itemSize() {
        return this.items.size();
    }
}
