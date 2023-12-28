package net.themcbrothers.lib.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredBlockEntityType<T extends BlockEntity> extends DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> {
    private DeferredBlockEntityType(ResourceKey<BlockEntityType<?>> key) {
        super(key);
    }

    public static <T extends BlockEntity> DeferredBlockEntityType<T> createBlockEntityType(ResourceLocation key) {
        return createBlockEntityType(ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, key));
    }

    public static <T extends BlockEntity> DeferredBlockEntityType<T> createBlockEntityType(ResourceKey<BlockEntityType<?>> key) {
        return new DeferredBlockEntityType<>(key);
    }
}
