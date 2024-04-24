package net.themcbrothers.lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.themcbrothers.lib.util.Version;

public abstract class BasePacketHandler {
    protected BasePacketHandler(IEventBus modEventBus, String modId, Version version, HandlerThread handlerThread) {
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            PayloadRegistrar registrarMainThread = event.registrar(modId).versioned(version.toString());
            PayloadRegistrar registrarNetworkThread = registrarMainThread.executesOn(HandlerThread.NETWORK);

            registerPackets(new PacketRegistrar(registrarMainThread));
            registerPacketsNetworkThread(new PacketRegistrar(registrarNetworkThread));
        });
    }

    protected abstract void registerPackets(PacketRegistrar registrar);

    protected abstract void registerPacketsNetworkThread(PacketRegistrar registrar);

    protected record PacketRegistrar(PayloadRegistrar registrar) {
        public <MSG extends PacketMessage> void commonBidirectional(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            registrar.commonBidirectional(id, reader, PacketMessage::handle);
        }

        public <MSG extends PacketMessage> void commonToServer(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            registrar.commonToServer(id, reader, PacketMessage::handle);
        }

        public <MSG extends PacketMessage> void commonToClient(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            registrar.commonToClient(id, reader, PacketMessage::handle);
        }

        public <MSG extends PacketMessage> void configurationBidirectional(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            registrar.configurationBidirectional(id, reader, PacketMessage::handle);
        }

        public <MSG extends PacketMessage> void configurationToServer(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            registrar.configurationToServer(id, reader, PacketMessage::handle);
        }

        public <MSG extends PacketMessage> void configurationToClient(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            registrar.configurationToClient(id, reader, PacketMessage::handle);
        }

        public <MSG extends PacketMessage> void playBidirectional(CustomPacketPayload.Type<MSG> id, StreamCodec<FriendlyByteBuf, MSG> reader) {
            registrar.playBidirectional(id, reader, PacketMessage::handle);
        }

        public <MSG extends PacketMessage> void playToServer(CustomPacketPayload.Type<MSG> id, StreamCodec<FriendlyByteBuf, MSG> reader) {
            registrar.playToServer(id, reader, PacketMessage::handle);
        }

        public <MSG extends PacketMessage> void playToClient(CustomPacketPayload.Type<MSG> id, StreamCodec<FriendlyByteBuf, MSG> reader) {
            registrar.playToClient(id, reader, PacketMessage::handle);
        }
    }
}
