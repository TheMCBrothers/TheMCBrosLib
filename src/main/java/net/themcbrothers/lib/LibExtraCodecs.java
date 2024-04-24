package net.themcbrothers.lib;


import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

/**
 * Some extra codecs
 */
public class LibExtraCodecs {
    public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> FLUID_STACK_OPTIONAL_LIST_STREAM_CODEC = FluidStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity));
    public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> FLUID_STACK_LIST_STREAM_CODEC = FluidStack.STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity));
}
