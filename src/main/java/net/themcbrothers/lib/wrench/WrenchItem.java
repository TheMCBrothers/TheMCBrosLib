package net.themcbrothers.lib.wrench;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class WrenchItem extends Item implements Wrench {
    public static final Map<Item, Collection<CreativeModeTab>> CREATIVE_TABS = Maps.newHashMap();

    public WrenchItem(Function<Properties, Properties> properties) {
        super(properties.apply(new Properties().stacksTo(1)));
        this.addToCreativeTabs(CreativeModeTabs.TOOLS_AND_UTILITIES);
    }

    public void addToCreativeTabs(CreativeModeTab... creativeModeTabs) {
        CREATIVE_TABS.computeIfAbsent(this, item -> Lists.newArrayList()).addAll(Arrays.asList(creativeModeTabs));
    }

    public void removeFromCreativeTabs(CreativeModeTab... creativeModeTabs) {
        CREATIVE_TABS.computeIfAbsent(this, item -> Lists.newArrayList()).removeAll(Arrays.asList(creativeModeTabs));
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
