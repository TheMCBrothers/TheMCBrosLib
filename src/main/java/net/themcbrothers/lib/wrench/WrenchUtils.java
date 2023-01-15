package net.themcbrothers.lib.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.themcbrothers.lib.util.TagUtils;

import javax.annotation.Nullable;

public class WrenchUtils {

    @Nullable
    public static Wrench getWrench(ItemStack stack) {
        if (stack.getItem() instanceof Wrench wrench)
            return wrench;
        else if (hasWrenchTag(stack))
            return Wrench.DEFAULT;
        return null;
    }

    public static void dismantleBlock(BlockState state, Level level, BlockPos pos, @Nullable BlockEntity tileEntity, @Nullable Entity entity, @Nullable ItemStack stack) {
        if (entity != null && stack != null) Block.dropResources(state, level, pos, tileEntity, entity, stack);
        else Block.dropResources(state, level, pos, tileEntity);
        level.removeBlock(pos, false);
    }

    public static boolean hasWrenchTag(ItemStack stack) {
        return stack.is(TagUtils.forgeItemTag("tools/wrench"))
                || stack.is(TagUtils.forgeItemTag("wrenches"))
                || stack.is(TagUtils.forgeItemTag("wrench"));
    }

}
