package net.themcbrothers.lib.crafting;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.themcbrothers.lib.LibExtraCodecs;

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

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidIngredient> CONTENTS_STREAM_CODEC = new StreamCodec<>() {
        private static final StreamCodec<RegistryFriendlyByteBuf, FluidIngredient> CODEC_STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.fromCodecWithRegistries(CODEC));

        @Override
        public void encode(RegistryFriendlyByteBuf buf, FluidIngredient ingredient) {
            LibExtraCodecs.FLUID_STACK_LIST_STREAM_CODEC.encode(buf, Arrays.asList(ingredient.getFluids()));
        }

        @Override
        public FluidIngredient decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            return fromValues(Stream.generate(() -> FluidStack.STREAM_CODEC.decode(buf)).limit(size).map(FluidValue::new));
        }
    };

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
            return fluidStack.isEmpty();
        } else {
            for (FluidStack stack : this.getFluids()) {
                if (FluidStack.isSameFluidSameComponents(fluidStack, stack) && fluidStack.getAmount() >= stack.getAmount()) {
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

    private static Codec<FluidIngredient> codec(boolean allowEmpty) {
        Codec<Value[]> codec = Codec.list(Value.CODEC)
                .comapFlatMap(
                        values -> !allowEmpty && values.isEmpty()
                                ? DataResult.error(() -> "Fluid array cannot be empty, at least one fluid must be defined")
                                : DataResult.success(values.toArray(new Value[0])),
                        List::of
                );
        return Codec.either(codec, Value.CODEC)
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
        static final Codec<FluidValue> CODEC = FluidStack.CODEC.xmap(FluidValue::new, fluidValue -> fluidValue.fluid);

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return FluidStack.matches(this.fluid, ((FluidValue) o).fluid);
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
                                ExtraCodecs.POSITIVE_INT.optionalFieldOf("amount", FluidType.BUCKET_VOLUME).forGetter(value -> value.amount))
                        .apply(instance, TagValue::new)
        );

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != this.getClass()) return false;
            TagValue value = (TagValue) obj;
            return value.tag.location().equals(this.tag.location()) && value.amount == this.amount;
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
        Codec<Value> CODEC = Codec.xor(FluidValue.CODEC, TagValue.CODEC)
                .xmap(either -> either.map(left -> left, right -> right), value -> {
                    if (value instanceof TagValue tagValue) {
                        return Either.right(tagValue);
                    } else if (value instanceof FluidValue fluidValue) {
                        return Either.left(fluidValue);
                    } else {
                        throw new UnsupportedOperationException("This is neither a fluid value nor a tag value.");
                    }
                });

        Collection<FluidStack> getFluids();
    }

    public final Value[] getValues() {
        return this.values;
    }
}
