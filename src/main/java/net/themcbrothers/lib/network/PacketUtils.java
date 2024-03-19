package net.themcbrothers.lib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.Optional;

public class PacketUtils {
    private PacketUtils() {
    }

    public static Optional<ServerPlayer> asServerPlayer(IPayloadContext context) {
        return context.player()
                .filter(ServerPlayer.class::isInstance)
                .map(ServerPlayer.class::cast);
    }

    public static <CLASS> Optional<CLASS> blockEntity(IPayloadContext context, BlockPos pos, Class<CLASS> clazz) {
        return blockEntity(context, pos)
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    public static Optional<BlockEntity> blockEntity(IPayloadContext context, BlockPos pos) {
        return context.level().map(level -> level.getBlockEntity(pos));
    }

    public static <CLASS extends AbstractContainerMenu> Optional<CLASS> container(IPayloadContext context, Class<CLASS> clazz) {
        return context.player()
                .map(player -> player.containerMenu)
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    /**
     * Send this message to the specified player.
     *
     * @param message - the message to send
     * @param player  - the player to send it to
     */
    public static <MSG extends CustomPacketPayload> void sendTo(MSG message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(message);
    }

    /**
     * Send this message to everyone connected to the server.
     *
     * @param message - message to send
     */
    public static <MSG extends CustomPacketPayload> void sendToAll(MSG message) {
        PacketDistributor.ALL.noArg().send(message);
    }

    /**
     * Send this message to everyone connected to the server if the server has loaded.
     *
     * @param message - message to send
     * @apiNote This is useful for reload listeners
     */
    public static <MSG extends CustomPacketPayload> void sendToAllIfLoaded(MSG message) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            // If the server has loaded, send to all players
            sendToAll(message);
        }
    }

    /**
     * Send this message to everyone within the supplied dimension.
     *
     * @param message   - the message to send
     * @param dimension - the dimension to target
     */
    public static <MSG extends CustomPacketPayload> void sendToDimension(MSG message, ResourceKey<Level> dimension) {
        PacketDistributor.DIMENSION.with(dimension).send(message);
    }

    /**
     * Send this message to the server.
     *
     * @param message - the message to send
     */
    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.SERVER.noArg().send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, Entity entity) {
        PacketDistributor.TRACKING_ENTITY.with(entity).send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToAllTrackingAndSelf(MSG message, Entity entity) {
        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity).send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, BlockEntity tile) {
        sendToAllTracking(message, Objects.requireNonNull(tile.getLevel()), tile.getBlockPos());
    }

    public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            // If we have a ServerWorld just directly figure out the ChunkPos to not require looking up the chunk
            // This provides a decent performance boost over using the packet distributor
            serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(p -> sendTo(message, p));
        } else {
            // Otherwise, fallback to entities tracking the chunk if some mod did something odd and our level is not a ServerWorld
            PacketDistributor.TRACKING_CHUNK.with(level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))).send(message);
        }
    }

    private static boolean isChunkTracked(ServerPlayer player, int chunkX, int chunkZ) {
        return player.getChunkTrackingView().contains(chunkX, chunkZ) && !player.connection.chunkSender.isPending(ChunkPos.asLong(chunkX, chunkZ));
    }
}