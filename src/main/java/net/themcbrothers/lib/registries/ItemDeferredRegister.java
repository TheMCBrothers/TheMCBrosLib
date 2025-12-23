package net.themcbrothers.lib.registries;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class ItemDeferredRegister extends DeferredRegister.Items {
    private ItemDeferredRegister(String namespace) {
        super(namespace);
    }

    @Deprecated
    @Override
    public <I extends Item> DeferredItem<I> register(String name, Function<Identifier, ? extends I> func) {
        return super.register(name, func);
    }

    @Deprecated
    @Override
    public <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> sup) {
        return super.register(name, sup);
    }

    public static ItemDeferredRegister create(String namespace) {
        return new ItemDeferredRegister(namespace);
    }
}
