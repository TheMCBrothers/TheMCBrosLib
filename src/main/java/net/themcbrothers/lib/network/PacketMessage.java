package net.themcbrothers.lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

public interface PacketMessage {
    void toBytes(FriendlyByteBuf buffer);

    void process(NetworkEvent.Context context);
}
