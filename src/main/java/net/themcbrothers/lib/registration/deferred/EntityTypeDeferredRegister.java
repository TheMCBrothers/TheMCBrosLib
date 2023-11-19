package net.themcbrothers.lib.registration.deferred;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Deferred register for an entity, building the type from a builder instance and adding an egg
 */
public class EntityTypeDeferredRegister extends DeferredRegisterWrapper<EntityType<?>> {
    private final DeferredRegister.Items itemRegister;

    public EntityTypeDeferredRegister(String modId) {
        super(Registries.ENTITY_TYPE, modId);
        this.itemRegister = DeferredRegister.createItems(modId);
    }

    @Override
    public void register(IEventBus bus) {
        super.register(bus);
        this.itemRegister.register(bus);
    }

    /**
     * Registers an entity type for the given entity type builder
     *
     * @param name Entity name
     * @param sup  Entity builder instance
     * @param <T>  Entity class type
     * @return Entity registry object
     */
    public <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, Supplier<EntityType.Builder<T>> sup) {
        return register.register(name, () -> sup.get().build(resourceName(name)));
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
        var object = register(name, sup);
        itemRegister.register(name + "_spawn_egg", () -> new DeferredSpawnEggItem(object, primary, secondary, new Item.Properties()));
        return object;
    }
}
