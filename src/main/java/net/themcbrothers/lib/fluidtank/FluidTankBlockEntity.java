package net.themcbrothers.lib.fluidtank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.themcbrothers.lib.LibDataComponents;
import net.themcbrothers.lib.TheMCBrosLib;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

import static net.neoforged.neoforge.fluids.FluidType.BUCKET_VOLUME;

public final class FluidTankBlockEntity extends BlockEntity {
    public static final int CAPACITY = 10 * BUCKET_VOLUME;
    private ResourceHandler<FluidResource> tank = new FluidStacksResourceHandler(1, CAPACITY) {
        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            FluidTankBlockEntity.this.setChanged();
        }
    };

    public FluidTankBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(TheMCBrosLib.FLUID_TANK_TYPE.get(), worldPosition, blockState);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(LibDataComponents.FLUID.get(), SimpleFluidContent.copyOf(FluidUtil.getStack(this.tank, 0)));
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        this.tank = new FluidStacksResourceHandler(NonNullList.of(FluidStack.EMPTY, components.getOrDefault(LibDataComponents.FLUID.get(), SimpleFluidContent.EMPTY).copy()), CAPACITY);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard("fluid");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.store("fluid", FluidStack.OPTIONAL_CODEC, FluidUtil.getStack(this.tank, 0));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        Optional<FluidStack> fluid = input.read("fluid", FluidStack.OPTIONAL_CODEC);
        this.tank = new FluidStacksResourceHandler(NonNullList.of(FluidStack.EMPTY, fluid.orElse(FluidStack.EMPTY)), CAPACITY);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        return this.saveCustomOnly(lookupProvider);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ResourceHandler<FluidResource> getTank() {
        return this.tank;
    }
}
