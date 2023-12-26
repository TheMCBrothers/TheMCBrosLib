package net.themcbrothers.lib.registries;

import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemDeferredRegister extends DeferredRegister.Items {
    private ItemDeferredRegister(String namespace) {
        super(namespace);
    }

    public static ItemDeferredRegister create(String namespace) {
        return new ItemDeferredRegister(namespace);
    }
}
