package net.themcbrothers.lib.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface WrenchableBlockEntity {
    default BlockEntity getWrenchableBlockEntity() {
        return (BlockEntity) this;
    }

    default boolean tryWrench(BlockState state, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Level level = getWrenchableBlockEntity().getLevel();
        BlockPos pos = getWrenchableBlockEntity().getBlockPos();

        assert level != null;
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty()) {
            Wrench wrench = WrenchUtils.getWrench(stack);
            if (wrench != null && wrench.canUseWrench(stack, player, pos)) {
                if (player.isSecondaryUseActive()) {
                    WrenchUtils.dismantleBlock(state, level, pos, getWrenchableBlockEntity(), null, null);
                    return true;
                }

                BlockState state1 = state.rotate(level, pos, Rotation.CLOCKWISE_90);
                if (state1 != state) {
                    level.setBlock(pos, state1, 1 | 2 | 16 | 32);
                    return true;
                }
            }
        }
        return false;
    }
}
