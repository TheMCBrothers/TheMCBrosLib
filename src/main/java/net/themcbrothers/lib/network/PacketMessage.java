package net.themcbrothers.lib.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Implement this interface on your packets
 */
public interface PacketMessage extends CustomPacketPayload {
    /**
     * Handle the packet with a given context
     *
     * @param context The context
     */
    void handle(IPayloadContext context);
}
