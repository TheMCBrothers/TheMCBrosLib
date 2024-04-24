package net.themcbrothers.lib;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Some extra data components
 *
 * @author TheMCLoveMan
 */
public class LibDataComponents {
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = TheMCBrosLib.DATA_COMPONENT_TYPES.register("energy",
            () -> DataComponentType.<Integer>builder()
                    .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    static void init() {
    }
}
