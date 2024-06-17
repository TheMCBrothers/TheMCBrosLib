package net.themcbrothers.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilities to help in custom models
 */
public final class ModelHelper {
    private static final Map<Block, ResourceLocation> TEXTURE_NAME_CACHE = new ConcurrentHashMap<>();

    /**
     * Gets the model for the given item
     *
     * @param item  Item provider
     * @param clazz Class type to cast result into
     * @param <T>   Class type
     * @return Item model, or null if its missing or the wrong class type
     */
    @Nullable
    public static <T extends BakedModel> T getBakedModel(ItemLike item, Class<T> clazz) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel baked = minecraft.getItemRenderer().getItemModelShaper().getItemModel(item.asItem());

        if (clazz.isInstance(baked)) {
            return clazz.cast(baked);
        }

        return null;
    }
}