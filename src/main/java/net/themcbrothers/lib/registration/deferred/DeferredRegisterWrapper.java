package net.themcbrothers.lib.registration.deferred;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;

/**
 * Wrapper for a {@link DeferredRegister}
 *
 * @param <T> Registry type
 */
public abstract class DeferredRegisterWrapper<T> {
    /**
     * DeferredRegister wrapped
     */
    protected final DeferredRegister<T> register;

    /**
     * Mod ID for registration
     */
    private final String modId;

    protected DeferredRegisterWrapper(ResourceKey<Registry<T>> registryKey, String modId) {
        this(DeferredRegister.create(registryKey, modId), modId);
    }

    protected DeferredRegisterWrapper(DeferredRegister<T> register, String modId) {
        this.register = register;
        this.modId = modId;
    }

    /**
     * Initializes this registry wrapper. Needs to be called during mod construction
     *
     * @param bus Mod Event Bus
     */
    public void register(IEventBus bus) {
        this.register.register(bus);
    }

    /**
     * @return All entries as {@link DeferredHolder}s
     */
    public Collection<DeferredHolder<T, ? extends T>> getEntries() {
        return this.register.getEntries();
    }

    protected ResourceLocation resource(String name) {
        return new ResourceLocation(this.modId, name);
    }

    protected String resourceName(String name) {
        return this.modId + ':' + name;
    }
}
