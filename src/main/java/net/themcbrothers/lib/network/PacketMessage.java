package net.themcbrothers.lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public interface PacketMessage {
    void toBytes(FriendlyByteBuf buffer);

    void process(Supplier<NetworkEvent.Context> context);
}
