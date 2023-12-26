package net.themcbrothers.lib.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class EntityTypeDeferredRegister extends DeferredRegister<EntityType<?>> {
    private EntityTypeDeferredRegister(String namespace) {
        super(Registries.ENTITY_TYPE, namespace);
    }

    @Override
    public <I extends EntityType<?>> DeferredEntityType<I> register(String name, Function<ResourceLocation, ? extends I> func) {
        return (DeferredEntityType<I>) super.register(name, func);
    }

    @Override
    public <I extends EntityType<?>> DeferredEntityType<I> register(String name, Supplier<? extends I> sup) {
        return this.register(name, key -> sup.get());
    }

    @Override
    protected <I extends EntityType<?>> DeferredHolder<EntityType<?>, I> createHolder(ResourceKey<? extends Registry<EntityType<?>>> registryKey, ResourceLocation key) {
        return DeferredEntityType.createEntity(ResourceKey.create(registryKey, key));
    }

    public static EntityTypeDeferredRegister create(String namespace) {
        return new EntityTypeDeferredRegister(namespace);
    }
}
