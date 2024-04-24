package net.themcbrothers.lib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

@SuppressWarnings("OptionalOfNullableMisuse")
public class PacketUtils {
    private PacketUtils() {
    }

    public static Optional<ServerPlayer> asServerPlayer(IPayloadContext context) {
        return Optional.ofNullable(context.player())
                .filter(ServerPlayer.class::isInstance)
                .map(ServerPlayer.class::cast);
    }

    public static <CLASS> Optional<CLASS> blockEntity(IPayloadContext context, BlockPos pos, Class<CLASS> clazz) {
        return blockEntity(context, pos)
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    public static Optional<BlockEntity> blockEntity(IPayloadContext context, BlockPos pos) {
        return Optional.ofNullable(context.player())
                .map(Entity::level)
                .map(level -> level.getBlockEntity(pos));
    }

    public static <CLASS extends AbstractContainerMenu> Optional<CLASS> container(IPayloadContext context, Class<CLASS> clazz) {
        return Optional.ofNullable(context.player())
                .map(player -> player.containerMenu)
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }
}