package net.themcbrothers.lib.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredEntityType<T extends EntityType<?>> extends DeferredHolder<EntityType<?>, T> {
    private DeferredEntityType(ResourceKey<EntityType<?>> key) {
        super(key);
    }

    public static <T extends EntityType<?>> DeferredEntityType<T> createEntity(ResourceLocation key) {
        return createEntity(ResourceKey.create(Registries.ENTITY_TYPE, key));
    }

    public static <T extends EntityType<?>> DeferredEntityType<T> createEntity(ResourceKey<EntityType<?>> key) {
        return new DeferredEntityType<>(key);
    }
}
