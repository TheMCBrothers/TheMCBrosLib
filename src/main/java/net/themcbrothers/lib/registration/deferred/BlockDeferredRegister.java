package net.themcbrothers.lib.registration.deferred;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.themcbrothers.lib.util.CreativeTabHelper;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Deferred register for {@link Block}s
 */
public class BlockDeferredRegister extends DeferredRegisterWrapper<Block> {
    private final DeferredRegister<Item> itemRegister;

    public BlockDeferredRegister(String modId) {
        super(Registries.BLOCK, modId);
        this.itemRegister = DeferredRegister.create(Registries.ITEM, modId);
    }

    @Override
    public void register(IEventBus bus) {
        super.register(bus);
        this.itemRegister.register(bus);
    }

    public <B extends Block> DeferredHolder<Block, B> registerNoItem(String name, Supplier<B> block) {
        return this.register.register(name, block);
    }

    public DeferredHolder<Block, Block> registerNoItem(String name, BlockBehaviour.Properties properties) {
        return this.registerNoItem(name, () -> new Block(properties));
    }

    @SafeVarargs
    public final <B extends Block> DeferredHolder<Block, B> register(String name, Supplier<B> block, Function<? super B, ? extends BlockItem> item, ResourceKey<CreativeModeTab>... tabs) {
        var blockObj = this.register.register(name, block);
        this.itemRegister.register(name, () -> item.apply(blockObj.get()));
        CreativeTabHelper.addToCreativeTabs(blockObj, tabs);
        return blockObj;
    }

    @SafeVarargs
    public final DeferredHolder<Block, Block> register(String name, BlockBehaviour.Properties properties, Function<? super Block, ? extends BlockItem> item, ResourceKey<CreativeModeTab>... tabs) {
        var blockObj = this.register.register(name, () -> new Block(properties));
        this.itemRegister.register(name, () -> item.apply(blockObj.get()));
        CreativeTabHelper.addToCreativeTabs(blockObj, tabs);
        return blockObj;
    }
}
