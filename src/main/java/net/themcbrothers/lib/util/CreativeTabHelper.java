package net.themcbrothers.lib.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.themcbrothers.lib.TheMCBrosLib;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Helps with the new 1.19.3 system for {@link CreativeModeTab}s
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = TheMCBrosLib.MOD_ID)
public class CreativeTabHelper {
    private static final Map<Supplier<? extends ItemLike>, Collection<CreativeModeTab>> ITEM_TO_TABS = Maps.newHashMap();

    /**
     * Adds a block or item to one or more creative mode tabs
     *
     * @param itemLike         Block or Item
     * @param creativeModeTabs Creative Mode Tabs
     */
    public static void addToCreativeTabs(Supplier<? extends ItemLike> itemLike, CreativeModeTab... creativeModeTabs) {
        ITEM_TO_TABS.computeIfAbsent(itemLike, item -> Lists.newArrayList()).addAll(Arrays.asList(creativeModeTabs));
    }

    @SubscribeEvent
    static void buildCreativeTabContent(final CreativeModeTabEvent.BuildContents event) {
        ITEM_TO_TABS.forEach(((item, creativeModeTabs) -> {
            if (creativeModeTabs.contains(event.getTab())) {
                event.accept(item.get());
            }
        }));
    }
}
