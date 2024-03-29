package net.themcbrothers.lib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * Helps with creating tags
 */
public final class TagUtils {
    private TagUtils() {
    }

    // ITEM TAGS

    public static TagKey<Item> forgeItemTag(String name) {
        return ItemTags.create(new ResourceLocation("forge", name));
    }

    public static TagKey<Item> modItemTag(String modID, String name) {
        return ItemTags.create(new ResourceLocation(modID, name));
    }

    // BLOCK TAGS

    public static TagKey<Block> forgeBlockTag(String name) {
        return BlockTags.create(new ResourceLocation("forge", name));
    }

    public static TagKey<Block> modBlockTag(String modID, String name) {
        return BlockTags.create(new ResourceLocation(modID, name));
    }

    // FLUID TAGS

    public static TagKey<Fluid> forgeFluidTag(String id) {
        return FluidTags.create(new ResourceLocation("forge", id));
    }

    public static TagKey<Fluid> modFluidTag(String modID, String id) {
        return FluidTags.create(new ResourceLocation(modID, id));
    }
}
