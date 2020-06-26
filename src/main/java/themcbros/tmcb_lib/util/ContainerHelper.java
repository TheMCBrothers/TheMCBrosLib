package themcbros.tmcb_lib.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;

public class ContainerHelper {

    public static <T extends TileEntity> T getTileEntity(Class<T> clazz, final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        final TileEntity tileEntity = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileEntity == null) return null;
        else if (clazz.isInstance(tileEntity)) {
            return clazz.cast(tileEntity);
        }
        return null;
    }

}
