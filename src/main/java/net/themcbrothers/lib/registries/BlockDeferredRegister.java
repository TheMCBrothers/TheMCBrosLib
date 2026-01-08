package net.themcbrothers.lib.registries;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Modified version of {@link DeferredRegister.Blocks} for automatically registering {@link BlockItem}s
 */
public class BlockDeferredRegister extends DeferredRegister.Blocks {
    private final ItemDeferredRegister itemRegister;

    private BlockDeferredRegister(String namespace, ItemDeferredRegister itemRegister) {
        super(namespace);
        this.itemRegister = itemRegister;
    }

    @Override
    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties) {
        DeferredBlock<B> holder = super.registerBlock(name, func, properties);
        this.itemRegister.registerSimpleBlockItem(holder);
        return holder;
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties, BiFunction<? super B, Item.Properties, ? extends BlockItem> blockItemFactory, Supplier<Item.Properties> itemProperties) {
        DeferredBlock<B> holder = super.registerBlock(name, func, properties);
        this.itemRegister.registerItem(name, props -> blockItemFactory.apply(holder.get(), props.useBlockDescriptionPrefix()), itemProperties);
        return holder;
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> properties, BiFunction<? super B, Item.Properties, ? extends BlockItem> blockItemFactory, UnaryOperator<Item.Properties> itemProperties) {
        return this.registerBlock(name, func, () -> properties.apply(BlockBehaviour.Properties.of()), blockItemFactory, () -> itemProperties.apply(new Item.Properties()));
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties, BiFunction<? super B, Item.Properties, ? extends BlockItem> blockItemFactory, UnaryOperator<Item.Properties> itemProperties) {
        return this.registerBlock(name, func, properties, blockItemFactory, () -> itemProperties.apply(new Item.Properties()));
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> properties, BiFunction<? super B, Item.Properties, ? extends BlockItem> blockItemFactory, Supplier<Item.Properties> itemProperties) {
        return this.registerBlock(name, func, () -> properties.apply(BlockBehaviour.Properties.of()), blockItemFactory, itemProperties);
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties, BiFunction<? super B, Item.Properties, ? extends BlockItem> blockItemFactory) {
        return this.registerBlock(name, func, properties, blockItemFactory, UnaryOperator.identity());
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> properties, BiFunction<? super B, Item.Properties, ? extends BlockItem> blockItemFactory) {
        return this.registerBlock(name, func, properties, blockItemFactory, UnaryOperator.identity());
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, BiFunction<? super B, Item.Properties, ? extends BlockItem> blockItemFactory) {
        return this.registerBlock(name, func, UnaryOperator.identity(), blockItemFactory, UnaryOperator.identity());
    }

    /**
     * Registers a simple block without an item.
     *
     * @param name  Registry name
     * @param props Block properties
     * @return Block holder object
     */
    public DeferredBlock<Block> registerNoItem(String name, BlockBehaviour.Properties props) {
        return super.registerBlock(name, Block::new, () -> props);
    }

    /**
     * Registers a simple block without an item.
     *
     * @param name  Registry name
     * @param props Block properties
     * @return Block holder object
     */
    public DeferredBlock<Block> registerNoItem(String name, Supplier<BlockBehaviour.Properties> props) {
        return super.registerBlock(name, Block::new, props);
    }

    /**
     * Registers a block without an item.
     *
     * @param name  Registry name
     * @param props Block properties
     * @return Block holder object
     */
    public DeferredBlock<Block> registerNoItem(String name, UnaryOperator<BlockBehaviour.Properties> props) {
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
        return super.registerBlock(name, func, () -> props);
    }

    /**
     * Registers a block without an item.
     *
     * @param name  Registry name
     * @param func  Block factory
     * @param props Block properties
     * @return Block holder object
     */
    public <B extends Block> DeferredBlock<B> registerNoItem(String name, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> props) {
        return super.registerBlock(name, func, props);
    }

    /**
     * Registers a block without an item.
     *
     * @param name  Registry name
     * @param func  Block factory
     * @param props Block properties
     * @return Block holder object
     */
    public <B extends Block> DeferredBlock<B> registerNoItem(String name, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> props) {
        return super.registerBlock(name, func, props);
    }

    @Deprecated
    @Override
    public <B extends Block> DeferredBlock<B> register(String name, Function<Identifier, ? extends B> func) {
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
