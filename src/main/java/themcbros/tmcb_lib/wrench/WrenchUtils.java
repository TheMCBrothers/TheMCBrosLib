package themcbros.tmcb_lib.wrench;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static themcbros.tmcb_lib.util.TagUtils.forgeItemTag;

public class WrenchUtils {

    @Nullable
    public static IWrench getWrench(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof IWrench)
            return (IWrench) item;
        else if (hasWrenchTag(item))
            return IWrench.DEFAULT;
        return null;
    }

    public static void dismantleBlock(BlockState state, World world, BlockPos pos, @Nullable TileEntity tileEntity, @Nullable Entity entity, @Nullable ItemStack stack) {
        if (entity != null && stack != null) Block.spawnDrops(state, world, pos, tileEntity, entity, stack);
        else Block.spawnDrops(state, world, pos, tileEntity);
        world.removeBlock(pos, false);
    }

    public static boolean hasWrenchTag(Item item) {
        return item.isIn(forgeItemTag("tools/wrench"))
                || item.isIn(forgeItemTag("wrenches"))
                || item.isIn(forgeItemTag("wrench"));
    }

}
