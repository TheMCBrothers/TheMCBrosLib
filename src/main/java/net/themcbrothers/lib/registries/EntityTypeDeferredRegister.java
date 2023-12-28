package net.themcbrothers.lib.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class EntityTypeDeferredRegister extends DeferredRegister<EntityType<?>> {
    private final ItemDeferredRegister itemRegister;

    private EntityTypeDeferredRegister(String namespace, ItemDeferredRegister itemRegister) {
        super(Registries.ENTITY_TYPE, namespace);
        this.itemRegister = itemRegister;
    }

    /**
     * Registers an entity type for the given entity type builder
     *
     * @param name Entity name
     * @param sup  Entity builder instance
     * @param <T>  Entity class type
     * @return Entity registry object
     */
    public <T extends Entity> DeferredEntityType<EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> sup) {
        return this.register(name, () -> sup.get().build(getNamespace() + ":" + name));
    }

    /**
     * Registers an entity type for the given entity type builder, and registers a spawn egg for it
     *
     * @param name      Entity name
     * @param sup       Entity builder instance
     * @param primary   Primary egg color
     * @param secondary Secondary egg color
     * @param <T>       Entity class type
     * @return Entity registry object
     */
    public <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerWithEgg(String name, Supplier<EntityType.Builder<T>> sup, int primary, int secondary) {
        var object = this.registerEntity(name, sup);
        this.itemRegister.register(name + "_spawn_egg", () -> new DeferredSpawnEggItem(object, primary, secondary, new Item.Properties()));
        return object;
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

    public static EntityTypeDeferredRegister create(String namespace, ItemDeferredRegister itemRegister) {
        return new EntityTypeDeferredRegister(namespace, itemRegister);
    }
}
