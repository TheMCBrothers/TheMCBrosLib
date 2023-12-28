package net.themcbrothers.lib.registries;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import jdk.jfr.Experimental;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Deferred register for {@link BlockEntityType}s
 */
@Experimental
@SuppressWarnings({"DataFlowIssue", "unchecked", "unused"})
public class BlockEntityTypeDeferredRegister extends DeferredRegister<BlockEntityType<?>> {
    private BlockEntityTypeDeferredRegister(String namespace) {
        super(Registries.BLOCK_ENTITY_TYPE, namespace);
    }

    /**
     * Gets the data fixer type for the block entity instance
     *
     * @param name Block entity name
     * @return Data fixer type
     */
    @Nullable
    private Type<?> getType(String name) {
        return Util.fetchChoiceType(References.BLOCK_ENTITY, getNamespace() + ":" + name);
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
        this.register(name, () -> BlockEntityType.Builder.<T>of(factory, block.get()).build(getType(name)));
        return DeferredBlockEntityType.createBlockEntityType(new ResourceLocation(getNamespace(), name));
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
            return new BlockEntityType<>(factory, blocks.build(), getType(name));
        });

        return DeferredBlockEntityType.createBlockEntityType(new ResourceLocation(getNamespace(), name));
    }

    @Deprecated
    @Override
    public <I extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, I> register(String name, Supplier<? extends I> sup) {
        return super.register(name, sup);
    }

    @Deprecated
    @Override
    public <I extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, I> register(String name, Function<ResourceLocation, ? extends I> func) {
        return super.register(name, func);
    }

    @Override
    protected <I extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, I> createHolder(ResourceKey<? extends Registry<BlockEntityType<?>>> registryKey, ResourceLocation key) {
        return (DeferredHolder<BlockEntityType<?>, I>) DeferredBlockEntityType.createBlockEntityType(ResourceKey.create(registryKey, key));
    }

    public static BlockEntityTypeDeferredRegister create(String namespace) {
        return new BlockEntityTypeDeferredRegister(namespace);
    }
}
