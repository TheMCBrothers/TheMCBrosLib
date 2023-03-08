package net.themcbrothers.lib.wrench;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class WrenchItem extends Item implements Wrench {
    private final Collection<CreativeModeTab> creativeModeTabs = Lists.newArrayList();

    public WrenchItem(Function<Properties, Properties> properties) {
        super(properties.apply(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS)));
    }

    public void addToCreativeTabs(CreativeModeTab... creativeModeTabs) {
        this.creativeModeTabs.addAll(Arrays.asList(creativeModeTabs));
    }

    public void removeFromCreativeTabs(CreativeModeTab... creativeModeTabs) {
        this.creativeModeTabs.removeAll(Arrays.asList(creativeModeTabs));
    }

    @Override
    public Collection<CreativeModeTab> getCreativeTabs() {
        return this.creativeModeTabs;
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
