package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.network.protocol.Packet;

public interface PacketReceivedEvent {
    void onPacketReceived(Packet<?> packet);
}