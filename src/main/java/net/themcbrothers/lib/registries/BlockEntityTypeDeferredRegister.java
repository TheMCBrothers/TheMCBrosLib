package net.themcbrothers.lib.registries;

import com.google.common.collect.ImmutableSet;
import jdk.jfr.Experimental;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Deferred register for {@link BlockEntityType}s
 */
@Experimental
@SuppressWarnings({"unchecked", "unused"})
public class BlockEntityTypeDeferredRegister extends DeferredRegister<BlockEntityType<?>> {
    private BlockEntityTypeDeferredRegister(String namespace) {
        super(Registries.BLOCK_ENTITY_TYPE, namespace);
    }

    /**
     * Registers a block entity type for a single block
     *
     * @param name    Block entity name
     * @param factory Block entity factory
     * @param block   Single block to add
     * @return Registry object instance
     */
    public <T extends BlockEntity> DeferredBlockEntityType<T> register(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, Supplier<? extends Block> block) {
        this.register(name, () -> new BlockEntityType<>(factory, block.get()));
        return DeferredBlockEntityType.createBlockEntityType(Identifier.fromNamespaceAndPath(getNamespace(), name));
    }

    /**
     * Registers a new block entity type using a block entity factory and a block supplier
     *
     * @param name           Block entity name
     * @param factory        Block entity supplier
     * @param blockCollector Function to get block list
     * @return Registry object instance
     */
    public <T extends BlockEntity> DeferredBlockEntityType<T> register(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, Consumer<ImmutableSet.Builder<Block>> blockCollector) {
        super.register(name, () -> {
            ImmutableSet.Builder<Block> blocks = new ImmutableSet.Builder<>();
            blockCollector.accept(blocks);
            return new BlockEntityType<>(factory, blocks.build());
        });

        return DeferredBlockEntityType.createBlockEntityType(Identifier.fromNamespaceAndPath(getNamespace(), name));
    }

    @Deprecated
    @Override
    public <I extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, I> register(String name, Supplier<? extends I> sup) {
        return super.register(name, sup);
    }

    @Deprecated
    @Override
    public <I extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, I> register(String name, Function<Identifier, ? extends I> func) {
        return super.register(name, func);
    }

    @Override
    protected <I extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, I> createHolder(ResourceKey<? extends Registry<BlockEntityType<?>>> registryKey, Identifier key) {
        return (DeferredHolder<BlockEntityType<?>, I>) DeferredBlockEntityType.createBlockEntityType(ResourceKey.create(registryKey, key));
    }

    public static BlockEntityTypeDeferredRegister create(String namespace) {
        return new BlockEntityTypeDeferredRegister(namespace);
    }
}
