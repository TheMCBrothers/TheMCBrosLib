package net.themcbrothers.lib;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * Tags used by our library mod
 */
public class LibraryTags {
    public static class Blocks {
        public static final TagKey<Block> WRENCHABLE = TagKey.create(Registries.BLOCK, TheMCBrosLib.rl("wrenchable"));
    }
}
