package net.themcbrothers.lib.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
public final class CreativeTabHelper {
    private CreativeTabHelper() {
    }

    private static final Map<Supplier<? extends ItemLike>, Collection<ResourceLocation>> ITEM_TO_TABS = Maps.newHashMap();

    /**
     * Adds a block or item to one or more creative mode tabs
     *
     * @param itemLike         Block or Item
     * @param creativeModeTabs Creative Mode Tabs
     */
    public static void addToCreativeTabs(Supplier<? extends ItemLike> itemLike, ResourceLocation... creativeModeTabs) {
        if (creativeModeTabs.length > 0) {
            ITEM_TO_TABS.computeIfAbsent(itemLike, item -> Lists.newArrayList()).addAll(Arrays.asList(creativeModeTabs));
        }
    }

    /**
     * Adds a block or item to one or more creative mode tabs
     *
     * @param itemLike         Block or Item
     * @param creativeModeTabs Creative Mode Tabs
     */
    @SafeVarargs
    public static void addToCreativeTabs(Supplier<? extends ItemLike> itemLike, ResourceKey<CreativeModeTab>... creativeModeTabs) {
        if (creativeModeTabs.length > 0) {
            ITEM_TO_TABS.computeIfAbsent(itemLike, item -> Lists.newArrayList()).addAll(Arrays.stream(creativeModeTabs).map(ResourceKey::location).toList());
        }
    }

    @SubscribeEvent
    static void buildCreativeTabContent(final BuildCreativeModeTabContentsEvent event) {
        ITEM_TO_TABS.forEach(((item, creativeModeTabs) -> {
            if (creativeModeTabs.contains(CreativeModeTabRegistry.getName(event.getTab()))) {
                event.accept(item.get());
            }
        }));
    }
}
