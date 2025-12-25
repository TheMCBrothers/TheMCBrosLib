package net.themcbrothers.lib.fluidtank;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.themcbrothers.lib.TheMCBrosLib;
import net.themcbrothers.lib.wrench.WrenchableBlock;

public final class FluidTankBlock extends BaseEntityBlock implements WrenchableBlock {
    public static final MapCodec<FluidTankBlock> CODEC = simpleCodec(FluidTankBlock::new);

    public FluidTankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return TheMCBrosLib.FLUID_TANK_TYPE.get().create(blockPos, blockState);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (this.tryWrench(state, level, pos, player, hand, hitResult)) {
            return InteractionResult.SUCCESS_SERVER;
        } else if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection())) {
            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof FluidTankBlockEntity fluidTank) {
            FluidStack stack = FluidUtil.getStack(fluidTank.getTank(), 0);
            return stack.getFluidType().getLightLevel(stack);
        }

        return super.getLightEmission(state, level, pos);
    }
}
