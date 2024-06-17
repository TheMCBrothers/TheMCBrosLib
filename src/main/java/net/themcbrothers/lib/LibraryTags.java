package net.themcbrothers.lib;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.themcbrothers.lib.util.TagUtils;

/**
 * Tags used by our library mod
 */
public class LibraryTags {
    public static class Blocks {
        public static final TagKey<Block> WRENCHABLE = TagKey.create(Registries.BLOCK, TheMCBrosLib.rl("wrenchable"));
    }

    public static class Items {
        public static final TagKey<Item> TOOLS_WRENCH = TagUtils.commonItemTag("tools/wrench");
    }
}
