package net.themcbrothers.lib.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDeferredRegister extends DeferredRegister.Blocks {
    private final ItemDeferredRegister itemRegister;

    private BlockDeferredRegister(String namespace, ItemDeferredRegister itemRegister) {
        super(namespace);
        this.itemRegister = itemRegister;
    }

    public <T extends Block> DeferredBlock<T> register(String name, Supplier<? extends T> block) {
        DeferredBlock<T> blockObject = super.register(name, block);
        this.itemRegister.registerSimpleBlockItem(blockObject);
        return blockObject;
    }

    public <T extends Block> DeferredBlock<T> register(String name, Supplier<T> block, Function<? super T, ? extends BlockItem> blockItemFactory) {
        DeferredBlock<T> holder = super.register(name, block);
        this.itemRegister.register(name, () -> blockItemFactory.apply(holder.get()));
        return holder;
    }

    public <T extends Block> DeferredBlock<T> registerNoItem(String name, Supplier<? extends T> block) {
        return super.register(name, block);
    }

    public static BlockDeferredRegister create(String namespace, ItemDeferredRegister itemRegister) {
        return new BlockDeferredRegister(namespace, itemRegister);
    }
}
