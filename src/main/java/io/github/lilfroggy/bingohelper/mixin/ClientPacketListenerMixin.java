package io.github.lilfroggy.bingohelper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lilfroggy.bingohelper.events.Events;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

	@Inject(method = "handleParticleEvent", at = @At("RETURN"))
	private void onParticle(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
		Events.PARTICLE_SPAWN.invoke(listener -> listener.onParticleSpawn(packet));
	}
}