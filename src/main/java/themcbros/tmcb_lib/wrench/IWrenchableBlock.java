package themcbros.tmcb_lib.wrench;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public interface IWrenchableBlock {

    default boolean tryWrench(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty()) {
            IWrench wrench = WrenchUtils.getWrench(stack);
            if (wrench != null && wrench.canUseWrench(stack, player, pos)) {
                if (player.isSneaking()) {
                    WrenchUtils.dismantleBlock(state, world, pos, null, null, null);
                    return true;
                }
                BlockState state1 = state.rotate(world, pos, Rotation.CLOCKWISE_90);
                if (state1 != state) {
                    world.setBlockState(pos, state1, 1 | 3 | 16 | 32);
                    return true;
                }
            }
        }
        return false;
    }

}
