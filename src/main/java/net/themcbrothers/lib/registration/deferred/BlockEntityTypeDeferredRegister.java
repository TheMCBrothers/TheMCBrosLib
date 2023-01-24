package net.themcbrothers.lib.registration.deferred;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Deferred register for {@link BlockEntityType}s
 */
public class BlockEntityTypeDeferredRegister extends DeferredRegisterWrapper<BlockEntityType<?>> {
    public BlockEntityTypeDeferredRegister(String modId) {
        super(Registries.BLOCK_ENTITY_TYPE, modId);
    }

    /**
     * Gets the data fixer type for the block entity instance
     *
     * @param name Block entity name
     * @return Data fixer type
     */
    @Nullable
    private Type<?> getType(String name) {
        return Util.fetchChoiceType(References.BLOCK_ENTITY, resourceName(name));
    }

    /**
     * Registers a block entity type for a single block
     *
     * @param name    Block entity name
     * @param factory Block entity factory
     * @param block   Single block to add
     * @return Registry object instance
     */
    @SuppressWarnings("DataFlowIssue")
    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, Supplier<? extends Block> block) {
        return this.register.register(name, () -> BlockEntityType.Builder.<T>of(factory, block.get()).build(getType(name)));
    }

    /**
     * Registers a new block entity type using a block entity factory and a block supplier
     *
     * @param name           Block entity name
     * @param factory        Block entity supplier
     * @param blockCollector Function to get block list
     * @return Registry object instance
     */
    @SuppressWarnings("DataFlowIssue")
    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, Consumer<ImmutableSet.Builder<Block>> blockCollector) {
        return this.register.register(name, () -> {
            ImmutableSet.Builder<Block> blocks = new ImmutableSet.Builder<>();
            blockCollector.accept(blocks);
            return new BlockEntityType<>(factory, blocks.build(), getType(name));
        });
    }
}
