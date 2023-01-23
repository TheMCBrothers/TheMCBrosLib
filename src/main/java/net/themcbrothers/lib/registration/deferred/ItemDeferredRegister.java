package net.themcbrothers.lib.registration.deferred;

import net.minecraft.core.registries.Registries;
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
    public <I extends Item> ItemObject<I> register(String name, Supplier<I> item, CreativeModeTab... tabs) {
        ItemObject<I> object = new ItemObject<>(this.register.register(name, item));
        CreativeTabHelper.addToCreativeTabs(object, tabs);
        return object;
    }

    public ItemObject<Item> register(String name, Item.Properties properties, CreativeModeTab... tabs) {
        ItemObject<Item> object = new ItemObject<>(this.register.register(name, () -> new Item(properties)));
        CreativeTabHelper.addToCreativeTabs(object, tabs);
        return object;
    }
}
