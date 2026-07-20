package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;

public interface ParticleSpawnEvent {
    void onParticleSpawn(ClientboundLevelParticlesPacket packet);
}