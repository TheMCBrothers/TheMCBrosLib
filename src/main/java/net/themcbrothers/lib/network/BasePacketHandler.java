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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.*;
import net.neoforged.neoforge.network.registration.IDirectionAwarePayloadHandlerBuilder;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.themcbrothers.lib.util.Version;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        <PAYLOAD extends CustomPacketPayload, HANDLER> IDirectionAwarePayloadHandlerBuilder<PAYLOAD, HANDLER> accept(IDirectionAwarePayloadHandlerBuilder<PAYLOAD, HANDLER> builder, HANDLER handler);
    }

    protected record PacketRegistrar(IPayloadRegistrar registrar, ContextAwareHandler contextAwareHandler) {
        private <MSG extends PacketMessage<IPayloadContext>> void common(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader, IPayloadHandler<MSG> handler) {
            registrar.common(id, reader, builder -> contextAwareHandler.accept(builder, handler));
        }

        public <MSG extends PacketMessage<IPayloadContext>> void common(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader) {
            common(id, reader, PacketMessage::handleMainThread);
        }

        public <MSG extends PacketMessage<IPayloadContext>> void commonNetworkThread(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader) {
            common(id, reader, PacketMessage::handle);
        }

        public PacketMessage<IPayloadContext> commonInstanced(ResourceLocation id, Consumer<IPayloadContext> handler) {
            return instanced(id, handler, this::common);
        }

        private <MSG extends PacketMessage<ConfigurationPayloadContext>> void configuration(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader, IConfigurationPayloadHandler<MSG> handler) {
            registrar.configuration(id, reader, builder -> contextAwareHandler.accept(builder, handler));
        }

        public void configuration(ResourceLocation id, FriendlyByteBuf.Reader<? extends PacketMessage<ConfigurationPayloadContext>> reader) {
            configuration(id, reader, PacketMessage::handleMainThread);
        }

        public void configurationNetworkThread(ResourceLocation id, FriendlyByteBuf.Reader<? extends PacketMessage<ConfigurationPayloadContext>> reader) {
            configuration(id, reader, PacketMessage::handle);
        }

        public PacketMessage<ConfigurationPayloadContext> configurationInstanced(ResourceLocation id, Consumer<ConfigurationPayloadContext> handler) {
            return instanced(id, handler, this::configuration);
        }

        private <MSG extends PacketMessage<PlayPayloadContext>> void play(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader, IPlayPayloadHandler<MSG> handler) {
            registrar.play(id, reader, builder -> contextAwareHandler.accept(builder, handler));
        }

        public void play(ResourceLocation id, FriendlyByteBuf.Reader<? extends PacketMessage<PlayPayloadContext>> reader) {
            play(id, reader, PacketMessage::handleMainThread);
        }

        public void playNetworkThread(ResourceLocation id, FriendlyByteBuf.Reader<? extends PacketMessage<PlayPayloadContext>> reader) {
            play(id, reader, PacketMessage::handle);
        }

        public PacketMessage<PlayPayloadContext> playInstanced(ResourceLocation id, Consumer<PlayPayloadContext> handler) {
            return instanced(id, handler, this::play);
        }

        private <CONTEXT extends IPayloadContext> PacketMessage<CONTEXT> instanced(ResourceLocation id, Consumer<CONTEXT> handler, BiConsumer<ResourceLocation, FriendlyByteBuf.Reader<PacketMessage<CONTEXT>>> registerMethod) {
            PacketMessage<CONTEXT> instance = new PacketMessage<>() {
                @Override
                public void write(FriendlyByteBuf buf) {
                }

                @Override
                public ResourceLocation id() {
                    return id;
                }

                @Override
                public void handle(CONTEXT context) {
                    handler.accept(context);
                }
            };

            registerMethod.accept(id, buf -> instance);
            return instance;
        }
    }
}
