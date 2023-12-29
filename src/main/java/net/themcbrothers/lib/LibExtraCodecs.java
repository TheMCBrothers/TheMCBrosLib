package net.themcbrothers.lib;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Optional;

/**
 * Some extra codecs
 */
public class LibExtraCodecs {
    public static final Codec<Fluid> FLUID_NON_EMPTY_CODEC = ExtraCodecs.validate(BuiltInRegistries.FLUID.byNameCodec(),
            fluid -> fluid == Fluids.EMPTY ? DataResult.error(() -> "Fluid must not be minecraft:empty") : DataResult.success(fluid));
    public static final Codec<FluidStack> FLUID_WITH_AMOUNT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    FLUID_NON_EMPTY_CODEC.fieldOf("fluid").forGetter(FluidStack::getFluid),
                    ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "amount", FluidType.BUCKET_VOLUME).forGetter(FluidStack::getAmount),
                    ExtraCodecs.strictOptionalField(CompoundTag.CODEC, "nbt").forGetter(stack -> Optional.ofNullable(stack.getTag())))
            .apply(instance, (fluid, amount, tag) -> {
                FluidStack stack = new FluidStack(fluid, amount);
                tag.ifPresent(stack::setTag);
                return stack;
            }));
}
