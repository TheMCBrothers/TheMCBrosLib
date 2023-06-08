package net.themcbrothers.lib.registration.deferred;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.themcbrothers.lib.registration.object.ItemObject;
import net.themcbrothers.lib.util.CreativeTabHelper;

import java.util.function.Supplier;

/**
 * Deferred register for {@link Item}s
 */
public class ItemDeferredRegister extends DeferredRegisterWrapper<Item> {
    public ItemDeferredRegister(String modId) {
        super(Registries.ITEM, modId);
    }

    /**
     * Registers an item
     *
     * @param name Registry name
     * @param item Item supplier
     * @param tabs Creative mode tabs
     * @param <I>  Item type
     * @return Item object
     */
    @SafeVarargs
    public final <I extends Item> ItemObject<I> register(String name, Supplier<I> item, ResourceKey<CreativeModeTab>... tabs) {
        ItemObject<I> object = new ItemObject<>(this.register.register(name, item));
        CreativeTabHelper.addToCreativeTabs(object, tabs);
        return object;
    }

    /**
     * Registers a standard item with the given properties
     *
     * @param name       Registry name
     * @param properties Item properties
     * @param tabs       Creative mode tabs
     * @return Item object
     */
    @SafeVarargs
    public final ItemObject<Item> register(String name, Item.Properties properties, ResourceKey<CreativeModeTab>... tabs) {
        ItemObject<Item> object = new ItemObject<>(this.register.register(name, () -> new Item(properties)));
        CreativeTabHelper.addToCreativeTabs(object, tabs);
        return object;
    }
}
