package themcbros.tmcb_lib.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface WrenchableBlock {
    default boolean tryWrench(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty()) {
            Wrench wrench = WrenchUtils.getWrench(stack);
            if (wrench != null && wrench.canUseWrench(stack, player, pos)) {
                if (player.isSecondaryUseActive()) {
                    WrenchUtils.dismantleBlock(state, world, pos, null, null, null);
                    return true;
                }

                BlockState state1 = state.rotate(world, pos, Rotation.CLOCKWISE_90);
                if (state1 != state) {
                    world.setBlock(pos, state1, 1 | 2 | 16 | 32);
                    return true;
                }
            }
        }
        return false;
    }
}
