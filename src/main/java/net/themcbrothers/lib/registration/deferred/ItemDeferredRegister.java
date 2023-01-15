package net.themcbrothers.lib.registration.deferred;

import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import net.themcbrothers.lib.registration.object.ItemObject;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Deferred register for {@link Item}s
 */
public class ItemDeferredRegister extends DeferredRegisterWrapper<Item> {
    public ItemDeferredRegister(String modId) {
        super(Registry.ITEM_REGISTRY, modId);
    }

    public <I extends Item> ItemObject<I> register(String name, Supplier<I> item) {
        return new ItemObject<>(this.register.register(name, item));
    }

    public ItemObject<Item> register(String name, Item.Properties properties) {
        return new ItemObject<>(this.register.register(name, () -> new Item(properties)));
    }

    /**
     * @return All entries as {@link RegistryObject}s
     */
    public Collection<RegistryObject<Item>> getEntries() {
        return this.register.getEntries();
    }
}
