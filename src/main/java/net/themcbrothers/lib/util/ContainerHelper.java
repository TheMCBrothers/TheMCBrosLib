package net.themcbrothers.lib.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ContainerHelper {
    /**
     * Returns the block entity for a container
     *
     * @param clazz Block entity class
     * @param inv   Player inventory
     * @param data  Buffer data
     * @return Block entity instance
     */
    @NotNull
    public static <T extends BlockEntity> T getBlockEntity(Class<T> clazz, final Inventory inv, final FriendlyByteBuf data) {
        Objects.requireNonNull(inv, "inv cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        final BlockEntity blockEntity = inv.player.level.getBlockEntity(data.readBlockPos());
        if (clazz.isInstance(blockEntity)) {
            return clazz.cast(blockEntity);
        }

        throw new IllegalStateException("BlockEntity is not correct! " + blockEntity);
    }
}
