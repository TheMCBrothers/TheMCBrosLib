package net.themcbrothers.lib.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.themcbrothers.lib.LibraryTags;
import net.themcbrothers.lib.util.CreativeTabHelper;

import java.util.function.Function;

public class WrenchItem extends Item implements Wrench {
    public WrenchItem(Function<Properties, Properties> properties) {
        super(properties.apply(new Properties().stacksTo(1)));
        CreativeTabHelper.addToCreativeTabs(() -> this, CreativeModeTabs.TOOLS_AND_UTILITIES.location());
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public boolean canUseWrench(ItemStack stack, Player player, BlockPos pos) {
        return true;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof WrenchableBlock) {
            // continues to the WrenchableBlock implementation
            return InteractionResult.PASS;
        }

        // if the player is not sneaking we want to rotate blocks
        if (player == null || !player.isSecondaryUseActive()) {
            if (state.getBlock() instanceof ChestBlock && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                // we don't want to rotate double chests
                return InteractionResult.PASS;
            }

            BlockState rotated = state.rotate(level, pos, Rotation.CLOCKWISE_90);

            if (rotated == state) {
                // block is not rotatable
                return InteractionResult.PASS;
            }

            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        }

        // from now on the player is sneaking

        if (this.canUseWrench(stack, player, pos) && state.is(LibraryTags.Blocks.WRENCHABLE)) {
            // dismantle block
            WrenchUtils.dismantleBlock(state, level, pos, level.getBlockEntity(pos), null, null);
            return InteractionResult.SUCCESS;
        }

        // just fail
        return InteractionResult.FAIL;
    }
}
