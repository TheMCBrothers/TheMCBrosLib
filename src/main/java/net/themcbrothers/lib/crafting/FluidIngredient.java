package net.themcbrothers.lib.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Fluid ingredient for recipes
 */
public class FluidIngredient implements Predicate<FluidStack> {
    public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());

    public static final Codec<FluidIngredient> CODEC = codec(true);
    public static final Codec<FluidIngredient> CODEC_NONEMPTY = codec(false);
    public static final Codec<List<FluidIngredient>> LIST_CODEC = CODEC.listOf();
    public static final Codec<List<FluidIngredient>> LIST_CODEC_NONEMPTY = CODEC_NONEMPTY.listOf();

    public final Value[] values;
    @Nullable
    private FluidStack[] fluidStacks;

    protected FluidIngredient(Stream<? extends Value> values) {
        this.values = values.toArray(Value[]::new);
    }

    private FluidIngredient(Value[] values) {
        this.values = values;
    }

    public FluidStack[] getFluids() {
        if (this.fluidStacks == null) {
            this.fluidStacks = Arrays.stream(this.values).flatMap(p_43916_ -> p_43916_.getFluids().stream()).distinct().toArray(FluidStack[]::new);
        }

        return this.fluidStacks;
    }

    @Override
    public boolean test(@Nullable FluidStack fluidStack) {
        if (fluidStack == null) {
            return false;
        } else if (this.isEmpty()) {
            return false;
        } else {
            for (FluidStack stack : this.getFluids()) {
                if (fluidStack.containsFluid(stack)) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getAmount(Fluid fluid) {
        return Arrays.stream(getFluids())
                .filter(fluidStack -> fluidStack.getFluid() == fluid)
                .mapToInt(FluidStack::getAmount)
                .findFirst()
                .orElse(0);
    }

    public boolean isEmpty() {
        return this.values.length == 0 || Arrays.stream(getFluids()).allMatch(FluidStack::isEmpty);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FluidIngredient ingredient && Arrays.equals(this.values, ingredient.values);
    }

    public static FluidIngredient fromValues(Stream<? extends Value> values) {
        FluidIngredient ingredient = new FluidIngredient(values);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static FluidIngredient of() {
        return EMPTY;
    }

    public static FluidIngredient of(Fluid fluid, int amount) {
        return of(new FluidStack(fluid, amount));
    }

    public static FluidIngredient of(FluidStack... stacks) {
        return of(Arrays.stream(stacks));
    }

    public static FluidIngredient of(Stream<FluidStack> stacks) {
        return fromValues(stacks.filter(stack -> !stack.isEmpty()).map(FluidValue::new));
    }

    public static FluidIngredient of(TagKey<Fluid> fluidTag, int amount) {
        return fromValues(Stream.of(new TagValue(fluidTag, amount)));
    }

    public static FluidIngredient fromJson(JsonElement element, boolean nonEmpty) {
        Codec<FluidIngredient> codec = nonEmpty ? CODEC : CODEC_NONEMPTY;
        return Util.getOrThrow(codec.parse(JsonOps.INSTANCE, element), IllegalStateException::new);
    }

    public JsonElement toJson(boolean allowEmpty) {
        Codec<FluidIngredient> codec = allowEmpty ? CODEC : CODEC_NONEMPTY;
        return Util.getOrThrow(codec.encodeStart(JsonOps.INSTANCE, this), IllegalStateException::new);
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf buffer) {
        var size = buffer.readVarInt();
        return new FluidIngredient(Stream.generate(() -> new FluidValue(buffer.readFluidStack())).limit(size));
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeCollection(Arrays.asList(this.getFluids()), FriendlyByteBuf::writeFluidStack);
    }

    private static Codec<FluidIngredient> codec(boolean allowEmpty) {
        Codec<Value[]> codec = Codec.list(Value.CODEC)
                .comapFlatMap(
                        values -> !allowEmpty && values.isEmpty()
                                ? DataResult.error(() -> "Fluid array cannot be empty, at least one fluid must be defined")
                                : DataResult.success(values.toArray(new Value[0])),
                        List::of
                );
        return ExtraCodecs.either(codec, Value.CODEC)
                .flatComapMap(
                        either -> either.map(FluidIngredient::new, values -> new FluidIngredient(new Value[]{values})),
                        ingredient -> {
                            if (ingredient.values.length == 1) {
                                return DataResult.success(Either.right(ingredient.values[0]));
                            } else {
                                return ingredient.values.length == 0 && !allowEmpty
                                        ? DataResult.error(() -> "Fluid array cannot be empty, at least one fluid must be defined")
                                        : DataResult.success(Either.left(ingredient.values));
                            }
                        }
                );
    }

    public record FluidValue(FluidStack fluid) implements Value {
        static final Codec<FluidValue> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(FluidStack.CODEC.fieldOf("fluid").forGetter(value -> value.fluid))
                        .apply(instance, FluidValue::new)
        );

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return this.fluid.isFluidStackIdentical(((FluidValue) o).fluid);
        }

        @Override
        public Collection<FluidStack> getFluids() {
            return Collections.singleton(this.fluid);
        }
    }

    public record TagValue(TagKey<Fluid> tag, int amount) implements Value {
        static final Codec<TagValue> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(value -> value.tag),
                                Codec.INT.fieldOf("amount").forGetter(value -> value.amount))
                        .apply(instance, TagValue::new)
        );

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != this.getClass()) return false;
            TagValue value = (TagValue) obj;
            return value.tag.location().equals(this.tag.location());
        }

        @Override
        public Collection<FluidStack> getFluids() {
            List<FluidStack> list = Lists.newArrayList();

            for (Holder<Fluid> holder : BuiltInRegistries.FLUID.getTagOrEmpty(this.tag)) {
                list.add(new FluidStack(holder.value(), this.amount));
            }

            return list;
        }
    }

    public interface Value {
        Codec<Value> CODEC = ExtraCodecs.xor(FluidValue.CODEC, TagValue.CODEC)
                .xmap(either -> either.map(left -> left, right -> right), value -> {
                    if (value instanceof TagValue tagValue) {
                        return Either.right(tagValue);
                    } else if (value instanceof FluidValue fluidValue) {
                        return Either.left(fluidValue);
                    } else {
                        throw new UnsupportedOperationException("This is neither an fluid value nor a tag value.");
                    }
                });

        Collection<FluidStack> getFluids();
    }

    public final Value[] getValues() {
        return this.values;
    }
}
