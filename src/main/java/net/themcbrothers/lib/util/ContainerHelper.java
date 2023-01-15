package net.themcbrothers.lib.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class ContainerHelper {
    public static <T extends BlockEntity> T getTileEntity(Class<T> clazz, final Inventory inv, final FriendlyByteBuf data) {
        Objects.requireNonNull(inv, "inv cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        final BlockEntity tileEntity = inv.player.level.getBlockEntity(data.readBlockPos());
        if (tileEntity == null) {
            return null;
        } else if (clazz.isInstance(tileEntity)) {
            return clazz.cast(tileEntity);
        }

        return null;
    }
}
