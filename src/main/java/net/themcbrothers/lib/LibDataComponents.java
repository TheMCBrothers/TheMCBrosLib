package net.themcbrothers.lib;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Some extra data components
 *
 * @author TheMCLoveMan
 */
public class LibDataComponents {
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = TheMCBrosLib.DATA_COMPONENT_TYPES
            .registerComponentType("energy", builder -> builder
                    .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID = TheMCBrosLib.DATA_COMPONENT_TYPES
            .registerComponentType("fluid", builder -> builder
                    .persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    static void init() {
    }
}
