package net.themcbrothers.lib.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
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
}
