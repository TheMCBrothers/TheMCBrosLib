package net.themcbrothers.lib.registries;

import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockDeferredRegister extends DeferredRegister.Blocks {
    private BlockDeferredRegister(String namespace) {
        super(namespace);
    }

    public static BlockDeferredRegister create(String namespace) {
        return new BlockDeferredRegister(namespace);
    }
}
