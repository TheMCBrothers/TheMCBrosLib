package net.themcbrothers.lib.registration.deferred;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.themcbrothers.lib.registration.object.ItemObject;
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

    public <B extends Block> RegistryObject<B> registerNoItem(String name, Supplier<B> block) {
        return this.register.register(name, block);
    }

    public RegistryObject<Block> registerNoItem(String name, BlockBehaviour.Properties properties) {
        return this.registerNoItem(name, () -> new Block(properties));
    }

    public <B extends Block> ItemObject<B> register(String name, Supplier<B> block, Function<? super B, ? extends BlockItem> item, CreativeModeTab... tabs) {
        RegistryObject<B> blockObj = this.register.register(name, block);
        this.itemRegister.register(name, () -> item.apply(blockObj.get()));
        CreativeTabHelper.addToCreativeTabs(blockObj, tabs);
        return new ItemObject<>(blockObj);
    }

    public ItemObject<Block> register(String name, BlockBehaviour.Properties properties, Function<? super Block, ? extends BlockItem> item, CreativeModeTab... tabs) {
        RegistryObject<Block> blockObj = this.register.register(name, () -> new Block(properties));
        this.itemRegister.register(name, () -> item.apply(blockObj.get()));
        CreativeTabHelper.addToCreativeTabs(blockObj, tabs);
        return new ItemObject<>(blockObj);
    }
}
