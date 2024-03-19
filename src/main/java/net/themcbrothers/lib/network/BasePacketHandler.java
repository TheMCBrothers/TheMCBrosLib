package net.themcbrothers.lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.*;
import net.neoforged.neoforge.network.registration.IDirectionAwarePayloadHandlerBuilder;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.themcbrothers.lib.util.Version;

public abstract class BasePacketHandler {
    protected BasePacketHandler(IEventBus modEventBus, String modId, Version version) {
        modEventBus.addListener(RegisterPayloadHandlerEvent.class, event -> {
            IPayloadRegistrar registrar = event.registrar(modId).versioned(version.toString());

            registerClientToServer(new PacketRegistrar(registrar, IDirectionAwarePayloadHandlerBuilder::server));
            registerServerToClient(new PacketRegistrar(registrar, IDirectionAwarePayloadHandlerBuilder::client));
        });
    }

    protected abstract void registerClientToServer(PacketRegistrar registrar);

    protected abstract void registerServerToClient(PacketRegistrar registrar);

    @FunctionalInterface
    private interface ContextAwareHandler {
        <PAYLOAD extends CustomPacketPayload, HANDLER> void accept(IDirectionAwarePayloadHandlerBuilder<PAYLOAD, HANDLER> builder, HANDLER handler);
    }

    protected record PacketRegistrar(IPayloadRegistrar registrar, ContextAwareHandler contextAwareHandler) {
        private <MSG extends PacketMessage<IPayloadContext>> void common(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader, IPayloadHandler<MSG> handler) {
            registrar.common(id, reader, builder -> contextAwareHandler.accept(builder, handler));
        }

        public <MSG extends PacketMessage<IPayloadContext>> void common(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            common(id, reader, PacketMessage::handleMainThread);
        }

        public <MSG extends PacketMessage<IPayloadContext>> void commonNetworkThread(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            common(id, reader, PacketMessage::handle);
        }

        private <MSG extends PacketMessage<ConfigurationPayloadContext>> void configuration(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader, IConfigurationPayloadHandler<MSG> handler) {
            registrar.configuration(id, reader, builder -> contextAwareHandler.accept(builder, handler));
        }

        public <MSG extends PacketMessage<ConfigurationPayloadContext>> void configuration(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            configuration(id, reader, PacketMessage::handleMainThread);
        }

        public <MSG extends PacketMessage<ConfigurationPayloadContext>> void configurationNetworkThread(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            configuration(id, reader, PacketMessage::handle);
        }

        private <MSG extends PacketMessage<PlayPayloadContext>> void play(CustomPacketPayload.Type<MSG> id, StreamCodec<? super FriendlyByteBuf, MSG> reader, IPlayPayloadHandler<MSG> handler) {
            registrar.play(id, reader, builder -> contextAwareHandler.accept(builder, handler));
        }

        public <MSG extends PacketMessage<PlayPayloadContext>> void play(CustomPacketPayload.Type<MSG> id, StreamCodec<FriendlyByteBuf, MSG> reader) {
            play(id, reader, PacketMessage::handleMainThread);
        }

        public <MSG extends PacketMessage<PlayPayloadContext>> void playNetworkThread(CustomPacketPayload.Type<MSG> id, StreamCodec<FriendlyByteBuf, MSG> reader) {
            play(id, reader, PacketMessage::handle);
        }
    }
}
