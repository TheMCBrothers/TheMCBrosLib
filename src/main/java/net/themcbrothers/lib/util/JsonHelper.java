package net.themcbrothers.lib.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

/**
 * Utilities to help in parsing JSON
 */
public class JsonHelper {
    private JsonHelper() {
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