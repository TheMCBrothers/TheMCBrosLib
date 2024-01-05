// MIT License
//
// Copyright (c) 2017-2023 Aidan C. Brady
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
package net.themcbrothers.lib.network;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;

public class PacketUtils {
    private PacketUtils() {
    }

    public static <KEY, V1, V2> void writeMultipleMaps(FriendlyByteBuf buffer, Map<KEY, V1> map1, Map<KEY, V2> map2, FriendlyByteBuf.Writer<KEY> keyWriter,
                                                       FriendlyByteBuf.Writer<V1> v1Writer, FriendlyByteBuf.Writer<V2> v2Writer) {
        if (map1.size() != map2.size()) {
            throw new IllegalArgumentException("Expected map1 and map2 to have the same size");
        }
        buffer.writeVarInt(map1.size());
        for (Map.Entry<KEY, V1> entry : map1.entrySet()) {
            KEY key = entry.getKey();
            keyWriter.accept(buffer, key);
            v1Writer.accept(buffer, entry.getValue());
            V2 v2 = map2.get(key);
            if (v2 == null) {
                throw new IllegalArgumentException("Expected maps to have the same keys but map2 was missing key " + key);
            }
            v2Writer.accept(buffer, v2);
        }
    }

    public static <KEY, V1, V2> Pair<Map<KEY, V1>, Map<KEY, V2>> readMultipleMaps(FriendlyByteBuf buffer, FriendlyByteBuf.Reader<KEY> keyReader, FriendlyByteBuf.Reader<V1> v1Reader, FriendlyByteBuf.Reader<V2> v2Reader) {
        return readMultipleMaps(buffer, Maps::newHashMapWithExpectedSize, Maps::newHashMapWithExpectedSize, keyReader, v1Reader, v2Reader);
    }

    public static <KEY, V1, V2, M1 extends Map<KEY, V1>, M2 extends Map<KEY, V2>> Pair<M1, M2> readMultipleMaps(FriendlyByteBuf buffer, IntFunction<M1> map1Factory, IntFunction<M2> map2Factory,
                                                                                                                FriendlyByteBuf.Reader<KEY> keyReader, FriendlyByteBuf.Reader<V1> v1Reader, FriendlyByteBuf.Reader<V2> v2Reader) {
        int size = buffer.readVarInt();
        M1 map1 = map1Factory.apply(size);
        M2 map2 = map2Factory.apply(size);
        for (int element = 0; element < size; element++) {
            KEY key = keyReader.apply(buffer);
            map1.put(key, v1Reader.apply(buffer));
            map2.put(key, v2Reader.apply(buffer));
        }
        return Pair.of(map1, map2);
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