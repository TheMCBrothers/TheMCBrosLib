package themcbros.tmcb_lib.wrench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IWrench {

    boolean canUseWrench(ItemStack stack, PlayerEntity player, BlockPos pos);

    IWrench DEFAULT = (stack, player, pos) -> false;

}
