package net.themcbrothers.lib.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

    public static TagKey<Item> commonItemTag(String name) {
        return commonTag(Registries.ITEM, name);
    }

    public static TagKey<Block> commonBlockTag(String name) {
        return commonTag(Registries.BLOCK, name);
    }

    public static TagKey<Fluid> commonFluidTag(String name) {
        return commonTag(Registries.FLUID, name);
    }

    public static <T> TagKey<T> commonTag(ResourceKey<? extends Registry<T>> registry, String name) {
        return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath("c", name));
    }
}
