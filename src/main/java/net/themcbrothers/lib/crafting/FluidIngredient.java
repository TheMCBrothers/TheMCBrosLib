package net.themcbrothers.lib.crafting;

import com.google.gson.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.themcbrothers.lib.util.JsonHelper;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Fluid ingredient for recipes
 */
public abstract class FluidIngredient {
    /**
     * Empty fluid ingredient, matches nothing
     */
    public static final FluidIngredient EMPTY = new Empty();
    /**
     * Fluid json serializer instance
     */
    public static Serializer SERIALIZER = new Serializer();

    /**
     * Cached list of display fluids
     */
    private List<FluidStack> displayFluids;

    /**
     * Checks if the given fluid matches this ingredient
     *
     * @param fluid Fluid to check
     * @return True if the fluid matches
     */
    public abstract boolean test(Fluid fluid);

    /**
     * Gets the amount of the given fluid needed for the recipe
     *
     * @param fluid Fluid to check
     * @return Amount of the fluid needed
     */
    public abstract int getAmount(Fluid fluid);

    /**
     * Checks if the given fluid stack argument matches this ingredient
     *
     * @param stack Fluid stack to check
     * @return True if the fluid matches this ingredient and the amount is equal or greater than this
     */
    public boolean test(FluidStack stack) {
        Fluid fluid = stack.getFluid();
        return stack.getAmount() >= getAmount(fluid) && test(stack.getFluid());
    }

    /**
     * Gets a list of fluid stacks contained in this ingredient for display
     *
     * @return List of fluid stacks for this ingredient
     */
    public List<FluidStack> getFluids() {
        if (this.displayFluids == null) {
            this.displayFluids = getAllFluids().stream().filter(stack -> {
                Fluid fluid = stack.getFluid();
                return fluid.isSource(fluid.defaultFluidState());
            }).collect(Collectors.toList());
        }

        return this.displayFluids;
    }

    /**
     * Gets a list of fluid stacks contained in this ingredient for display, may include flowing fluids
     *
     * @return List of fluid stacks for this ingredient
     */
    protected abstract List<FluidStack> getAllFluids();

    /**
     * Serializes the Fluid Ingredient into JSON
     *
     * @return FluidIngredient JSON
     */
    public abstract JsonElement serialize();

    /**
     * Writes the ingredient into the packet buffer
     *
     * @param buffer Packet buffer instance
     */
    public void write(FriendlyByteBuf buffer) {
        Collection<FluidStack> fluids = getAllFluids();
        buffer.writeInt(fluids.size());
        for (FluidStack stack : fluids) {
            buffer.writeUtf(String.valueOf(ForgeRegistries.FLUIDS.getKey(stack.getFluid())));
            buffer.writeInt(stack.getAmount());
        }
    }

    /*
     * Instance creation
     */

    /**
     * Creates a new ingredient using the given fluid and amount
     *
     * @param fluid  Fluid to check
     * @param amount Minimum fluid amount
     * @return Fluid ingredient for this fluid
     */
    public static FluidIngredient of(Fluid fluid, int amount) {
        return new FluidIngredient.FluidMatch(fluid, amount);
    }

    /**
     * Creates a new ingredient using the given fluid stack
     *
     * @param stack Fluid stack
     * @return Fluid ingredient for this fluid stack
     */
    public static FluidIngredient of(FluidStack stack) {
        return of(stack.getFluid(), stack.getAmount());
    }

    /**
     * Creates a new fluid ingredient from the given tag
     *
     * @param fluid  Fluid tag
     * @param amount Minimum fluid amount
     * @return Fluid ingredient from a tag
     */
    public static FluidIngredient of(TagKey<Fluid> fluid, int amount) {
        return new FluidIngredient.TagMatch(fluid, amount);
    }

    /**
     * Creates a new compound ingredient from the given list of ingredients
     *
     * @param ingredients Ingredient list
     * @return Compound ingredient
     */
    public static FluidIngredient of(FluidIngredient... ingredients) {
        return new FluidIngredient.Compound(ingredients);
    }

    /*
     * JSON deserializing
     */

    /**
     * Deserializes the fluid ingredient from JSON
     *
     * @param parent Parent containing the fluid JSON
     * @param name   Name of the key to fetch from the parent object
     * @return Fluid ingredient instance
     * @throws JsonSyntaxException if syntax is invalid
     */
    public static FluidIngredient deserialize(JsonObject parent, String name) {
        return deserialize(JsonHelper.getElement(parent, name), name);
    }

    /**
     * Deserializes the fluid ingredient from JSON
     *
     * @param json Json element instance
     * @param name Name of the object for error messages
     * @return Fluid ingredient instance
     * @throws JsonSyntaxException if syntax is invalid
     */
    public static FluidIngredient deserialize(JsonElement json, String name) {
        // single ingredient object
        if (json.isJsonObject()) {
            return deserializeObject(json.getAsJsonObject());
        }

        // array
        if (json.isJsonArray()) {
            return Compound.deserialize(json.getAsJsonArray(), name);
        }

        throw new JsonSyntaxException("Fluid ingredient " + name + " must be either an object or array");
    }

    /**
     * Deserializes the fluid ingredient from JSON
     *
     * @param json JSON object
     * @return Fluid Ingredient
     * @throws JsonSyntaxException if syntax is invalid
     */
    private static FluidIngredient deserializeObject(JsonObject json) {
        if (json.entrySet().isEmpty()) {
            return EMPTY;
        }

        // fluid match
        if (json.has("name")) {
            // don't set both, obviously an error
            if (json.has("tag")) {
                throw new JsonSyntaxException("An ingredient entry is either a tag or an fluid, not both");
            }

            // parse a fluid
            return FluidMatch.deserialize(json);
        }

        // tag match
        if (json.has("tag")) {
            return TagMatch.deserialize(json);
        }

        throw new JsonSyntaxException("An ingredient entry needs either a tag or an fluid");
    }

    /*
     * Packet buffers
     */

    /**
     * Reads a fluid ingredient from the packet buffer
     *
     * @param buffer Buffer instance
     * @return Fluid ingredient instance
     */
    public static FluidIngredient read(FriendlyByteBuf buffer) {
        int count = buffer.readInt();
        FluidIngredient[] ingredients = new FluidIngredient[count];
        for (int i = 0; i < count; i++) {
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readUtf(32767)));
            if (fluid == null) {
                fluid = Fluids.EMPTY;
            }
            int amount = buffer.readInt();
            ingredients[i] = of(fluid, amount);
        }
        // if a single ingredient, do not wrap in compound
        if (count == 1) {
            return ingredients[0];
        }
        // compound for anything else
        return of(ingredients);
    }

    /**
     * Empty fluid ingredient
     */
    private static class Empty extends FluidIngredient {
        private Empty() {
        }

        @Override
        public boolean test(Fluid fluid) {
            return fluid == Fluids.EMPTY;
        }

        @Override
        public boolean test(FluidStack stack) {
            return stack.isEmpty();
        }

        @Override
        public int getAmount(Fluid fluid) {
            return 0;
        }

        @Override
        protected List<FluidStack> getAllFluids() {
            return Collections.emptyList();
        }

        @Override
        public JsonElement serialize() {
            return new JsonObject();
        }
    }

    /**
     * Fluid ingredient that matches a single fluid
     */
    private static class FluidMatch extends FluidIngredient {
        private final Fluid fluid;
        private final int amount;

        private FluidMatch(Fluid fluid, int amount) {
            this.fluid = fluid;
            this.amount = amount;
        }

        @Override
        public boolean test(Fluid fluid) {
            return fluid == this.fluid;
        }

        @Override
        public int getAmount(Fluid fluid) {
            return this.amount;
        }

        @Override
        protected List<FluidStack> getAllFluids() {
            return Collections.singletonList(new FluidStack(this.fluid, this.amount));
        }

        @Override
        public JsonElement serialize() {
            JsonObject object = new JsonObject();
            object.addProperty("name", String.valueOf(ForgeRegistries.FLUIDS.getKey(this.fluid)));
            object.addProperty("amount", amount);
            return object;
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            // count
            buffer.writeInt(1);
            // single fluid
            buffer.writeUtf(String.valueOf(ForgeRegistries.FLUIDS.getKey(this.fluid)));
            buffer.writeInt(this.amount);
        }

        /**
         * Deserializes the ingredient from JSON
         *
         * @param json JSON object
         * @return Fluid ingredient instance
         */
        private static FluidMatch deserialize(JsonObject json) {
            String fluidName = GsonHelper.getAsString(json, "name");
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
            if (fluid == null || fluid == Fluids.EMPTY) {
                throw new JsonSyntaxException("Unknown fluid '" + fluidName + "'");
            }
            int amount = GsonHelper.getAsInt(json, "amount");
            return new FluidMatch(fluid, amount);
        }
    }

    /**
     * Fluid ingredient that matches a tag
     */
    private static class TagMatch extends FluidIngredient {
        private final TagKey<Fluid> tag;
        private final int amount;

        private TagMatch(TagKey<Fluid> tag, int amount) {
            this.tag = tag;
            this.amount = amount;
        }

        @Override
        public boolean test(Fluid fluid) {
            return ForgeRegistries.FLUIDS.getHolder(fluid).orElseThrow().is(this.tag);
        }

        @Override
        public int getAmount(Fluid fluid) {
            return this.amount;
        }

        @Override
        protected List<FluidStack> getAllFluids() {
            return StreamSupport.stream(Registry.FLUID.getTagOrEmpty(tag).spliterator(), false)
                    .filter(Holder::isBound)
                    .map(fluid -> new FluidStack(fluid.value(), amount))
                    .toList();
        }

        @Override
        public JsonElement serialize() {
            JsonObject object = new JsonObject();
            object.addProperty("tag", this.tag.location().toString());
            object.addProperty("amount", amount);
            return object;
        }

        /**
         * Deserializes the ingredient from JSON
         *
         * @param json JSON object
         * @return Fluid ingredient instance
         */
        private static TagMatch deserialize(JsonObject json) {
            TagKey<Fluid> tag = TagKey.create(Registry.FLUID_REGISTRY, JsonHelper.getResourceLocation(json, "tag"));
            int amount = GsonHelper.getAsInt(json, "amount");
            return new TagMatch(tag, amount);
        }
    }

    /**
     * Fluid ingredient that matches a list of ingredients
     */
    private static class Compound extends FluidIngredient {
        private final List<FluidIngredient> ingredients;

        private Compound(FluidIngredient[] ingredients) {
            this.ingredients = Arrays.asList(ingredients);
        }

        @Override
        public boolean test(Fluid fluid) {
            return ingredients.stream().anyMatch(ingredient -> ingredient.test(fluid));
        }

        @Override
        public boolean test(FluidStack stack) {
            return ingredients.stream().anyMatch(ingredient -> ingredient.test(stack));
        }

        @Override
        public int getAmount(Fluid fluid) {
            return ingredients.stream()
                    .filter(ingredient -> ingredient.test(fluid))
                    .mapToInt(ingredient -> ingredient.getAmount(fluid))
                    .findFirst()
                    .orElse(0);
        }

        @Override
        public List<FluidStack> getAllFluids() {
            return ingredients.stream()
                    .flatMap(ingredient -> ingredient.getFluids().stream())
                    .collect(Collectors.toList());
        }

        @Override
        public JsonElement serialize() {
            return ingredients.stream()
                    .map(FluidIngredient::serialize)
                    .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
        }

        /**
         * Deserializes a compound ingredient from JSON
         *
         * @param array JSON array
         * @param name  Array key
         * @return Compound fluid ingredient instance
         */
        private static Compound deserialize(JsonArray array, String name) {
            // size must be valid
            int size = array.size();
            if (size == 0) {
                throw new JsonSyntaxException("Fluid array cannot be empty, at least one fluid must be defined");
            }

            // parse all ingredients
            FluidIngredient[] ingredients = new FluidIngredient[size];
            for (int i = 0; i < size; i++) {
                // no reason to an array in an array
                ingredients[i] = deserializeObject(GsonHelper.convertToJsonObject(array.get(i), name + "[" + i + "]"));
            }
            return new Compound(ingredients);
        }
    }

    /**
     * Json serializer for fluids
     */
    public static class Serializer implements JsonDeserializer<FluidIngredient>, JsonSerializer<FluidIngredient> {
        private Serializer() {
        }

        @Override
        public FluidIngredient deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return FluidIngredient.deserialize(json, "ingredient");
        }

        @Override
        public JsonElement serialize(FluidIngredient src, Type typeOfSrc, JsonSerializationContext context) {
            return src.serialize();
        }
    }
}
