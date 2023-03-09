package net.themcbrothers.lib.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utilities to help in parsing JSON
 */
public final class JsonHelper {
    private JsonHelper() {
    }

    /**
     * Parses a list from an JsonArray
     *
     * @param array  Json array
     * @param name   Json key of the array
     * @param mapper Mapper from the element object and name to new object
     * @param <T>    Output type
     * @return List of output objects
     */
    public static <T> List<T> parseList(JsonArray array, String name, BiFunction<JsonElement, String, T> mapper) {
        if (array.size() == 0) {
            throw new JsonSyntaxException(name + " must have at least 1 element");
        }

        // build the list
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (int i = 0; i < array.size(); i++) {
            builder.add(mapper.apply(array.get(i), name + "[" + i + "]"));
        }

        return builder.build();
    }

    /**
     * Parses a list from an JsonArray
     *
     * @param array  Json array
     * @param name   Json key of the array
     * @param mapper Mapper from the json object to new object
     * @param <T>    Output type
     * @return List of output objects
     */
    public static <T> List<T> parseList(JsonArray array, String name, Function<JsonObject, T> mapper) {
        return parseList(array, name, (element, s) -> mapper.apply(GsonHelper.convertToJsonObject(element, s)));
    }

    /**
     * Gets an element from JSON
     *
     * @param json       Object parent
     * @param memberName Name to get
     * @return JsonElement found
     * @throws JsonSyntaxException if element is missing
     */
    public static JsonElement getElement(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return json.get(memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + "");
        }
    }

    /**
     * Gets a {@link ResourceLocation} from JSON
     *
     * @param json JSON object
     * @param key  Key to fetch
     * @return Resource location parsed
     * @throws JsonSyntaxException if failed
     */
    public static ResourceLocation getResourceLocation(JsonObject json, String key) {
        String text = GsonHelper.getAsString(json, key);
        ResourceLocation location = ResourceLocation.tryParse(text);
        if (location == null) {
            throw new JsonSyntaxException("Expected " + key + " to be a Resource location, was '" + text + "'");
        }

        return location;
    }
}