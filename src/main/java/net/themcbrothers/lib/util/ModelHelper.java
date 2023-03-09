package net.themcbrothers.lib.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.VertexTransformer;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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
        //noinspection ConstantConditions  null during run data
        if (minecraft == null) {
            return null;
        }
        BakedModel baked = minecraft.getItemRenderer().getItemModelShaper().getItemModel(item.asItem());
        if (clazz.isInstance(baked)) {
            return clazz.cast(baked);
        }
        return null;
    }

    /* JSON */

    /**
     * Converts a JSON float array to the specified object
     *
     * @param json   JSON object
     * @param name   Name of the array in the object to fetch
     * @param size   Expected array size
     * @param mapper Functon to map from the array to the output type
     * @param <T>    Output type
     * @return Vector3f of data
     * @throws JsonParseException If there is no array or the length is wrong
     */
    public static <T> T arrayToObject(JsonObject json, String name, int size, Function<float[], T> mapper) {
        JsonArray array = GsonHelper.getAsJsonArray(json, name);
        if (array.size() != size) {
            throw new JsonParseException("Expected " + size + " " + name + " values, found: " + array.size());
        }
        float[] vec = new float[size];
        for (int i = 0; i < size; ++i) {
            vec[i] = GsonHelper.convertToFloat(array.get(i), name + "[" + i + "]");
        }
        return mapper.apply(vec);
    }

    /**
     * Converts a JSON array with 3 elements into a Vector3f
     *
     * @param json JSON object
     * @param name Name of the array in the object to fetch
     * @return Vector3f of data
     * @throws JsonParseException If there is no array or the length is wrong
     */
    public static Vector3f arrayToVector(JsonObject json, String name) {
        return arrayToObject(json, name, 3, arr -> new Vector3f(arr[0], arr[1], arr[2]));
    }

    /**
     * Gets a rotation from JSON
     *
     * @param json JSON parent
     * @return Integer of 0, 90, 180, or 270
     */
    public static int getRotation(JsonObject json, String key) {
        int i = GsonHelper.getAsInt(json, key, 0);
        if (i >= 0 && i % 90 == 0 && i / 90 <= 3) {
            return i;
        } else {
            throw new JsonParseException("Invalid '" + key + "' " + i + " found, only 0/90/180/270 allowed");
        }
    }

    public static BakedQuad colorQuad(int color, BakedQuad quad) {
        ColorTransformer transformer = new ColorTransformer(color, quad);
        quad.pipe(transformer);
        return transformer.build();
    }


    private static class ColorTransformer extends VertexTransformer {

        private final float r, g, b, a;

        public ColorTransformer(int color, BakedQuad quad) {
            super(new BakedQuadBuilder(quad.getSprite()));

            int a = (color >> 24);
            if (a == 0) {
                a = 255;
            }
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color >> 0) & 0xFF;

            this.r = (float) r / 255f;
            this.g = (float) g / 255f;
            this.b = (float) b / 255f;
            this.a = (float) a / 255f;
        }

        @Override
        public void put(int element, float... data) {
            VertexFormatElement.Usage usage = this.parent.getVertexFormat().getElements().get(element).getUsage();

            // transform normals and position
            if (usage == VertexFormatElement.Usage.COLOR && data.length >= 4) {
                data[0] = this.r;
                data[1] = this.g;
                data[2] = this.b;
                data[3] = this.a;
            }
            super.put(element, data);
        }

        public BakedQuad build() {
            return ((BakedQuadBuilder) this.parent).build();
        }
    }
}