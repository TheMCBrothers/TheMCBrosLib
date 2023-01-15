package net.themcbrothers.lib.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface Wrench {
    boolean canUseWrench(ItemStack stack, Player player, BlockPos pos);

    Wrench DEFAULT = (stack, player, pos) -> true;
}
