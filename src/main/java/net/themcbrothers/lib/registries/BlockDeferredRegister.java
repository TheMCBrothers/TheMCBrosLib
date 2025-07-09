package net.themcbrothers.lib.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Modified version of {@link DeferredRegister.Blocks} for automatically registering {@link BlockItem}s
 */
public class BlockDeferredRegister extends DeferredRegister.Blocks {
    private final ItemDeferredRegister itemRegister;

    private BlockDeferredRegister(String namespace, ItemDeferredRegister itemRegister) {
        super(namespace);
        this.itemRegister = itemRegister;
    }

    /**
     * Registers a simple {@link Block} with a simple {@link BlockItem}
     *
     * @param name  Registry name
     * @param props Block properties
     * @return Block holder object
     */
    @Override
    public DeferredBlock<Block> registerSimpleBlock(String name, BlockBehaviour.Properties props) {
        return this.registerBlock(name, Block::new, props);
    }

    /**
     * Registers a {@link Block} with a simple {@link BlockItem}
     *
     * @param name  Registry name
     * @param func  Block factory
     * @param props Block properties
     * @param <B>   Block type
     * @return Block holder object
     */
    @Override
    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
        DeferredBlock<B> holder = super.registerBlock(name, func, props);
        this.itemRegister.registerSimpleBlockItem(holder);
        return holder;
    }

    /**
     * Registers a {@link Block} with a {@link BlockItem} using the given factories
     *
     * @param name             Registry name
     * @param blockFactory     Block factory
     * @param props            Block properties
     * @param blockItemFactory Item factory
     * @param <B>              Block type
     * @return Block holder object
     */
    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> blockFactory, BlockBehaviour.Properties props, BiFunction<? super B, Item.Properties, ? extends BlockItem> blockItemFactory) {
        DeferredBlock<B> holder = super.registerBlock(name, blockFactory, props);
        this.itemRegister.registerItem(name, properties -> blockItemFactory.apply(holder.get(), properties.useBlockDescriptionPrefix()));
        return holder;
    }

    /**
     * Registers a {@link Block} with a {@link BlockItem} using the given factories
     *
     * @param name             Registry name
     * @param blockFactory     Block factory
     * @param props            Block properties
     * @param blockItemFactory Item factory
     * @param itemProps        Item properties
     * @param <B>              Block type
     * @return Block holder object
     */
    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> blockFactory, BlockBehaviour.Properties props, BiFunction<? super B, Item.Properties, ? extends BlockItem> blockItemFactory, Item.Properties itemProps) {
        DeferredBlock<B> holder = super.registerBlock(name, blockFactory, props);
        this.itemRegister.registerItem(name, properties -> blockItemFactory.apply(holder.get(), properties), itemProps.useBlockDescriptionPrefix());
        return holder;
    }

    /**
     * Registers a simple block without an item.
     *
     * @param name  Registry name
     * @param props Block properties
     * @return Block holder object
     */
    public DeferredBlock<Block> registerNoItem(String name, BlockBehaviour.Properties props) {
        return super.registerBlock(name, Block::new, props);
    }

    /**
     * Registers a block without an item.
     *
     * @param name  Registry name
     * @param func  Block factory
     * @param props Block properties
     * @return Block holder object
     */
    public <B extends Block> DeferredBlock<B> registerNoItem(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
        return super.registerBlock(name, func, props);
    }

    @Deprecated
    @Override
    public <B extends Block> DeferredBlock<B> register(String name, Function<ResourceLocation, ? extends B> func) {
        return super.register(name, func);
    }

    @Deprecated
    @Override
    public <B extends Block> DeferredBlock<B> register(String name, Supplier<? extends B> sup) {
        return super.register(name, sup);
    }

    /**
     * Creates a {@link BlockDeferredRegister}
     *
     * @param namespace    Mod ID
     * @param itemRegister An instance of {@link ItemDeferredRegister}
     * @return Fresh instance
     */
    public static BlockDeferredRegister create(String namespace, ItemDeferredRegister itemRegister) {
        return new BlockDeferredRegister(namespace, itemRegister);
    }
}
