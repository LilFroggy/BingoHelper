package io.github.lilfroggy.bingohelper.mixin;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ChatInterceptorMixin {
    private static final String CREATE_PROFILE_MESSAGE = "Welcome to SkyBlock Bingo!";

    @Inject(method = "handleSystemChat", at = @At("HEAD"))
    private void onGameMessage(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        if (!Minecraft.getInstance().isSameThread()) {
            return;
        }
        
        String formatted = packet.content().getString();
        String unformatted = ChatLib.removeFormatting(formatted);
        
        if (packet.overlay()) {
            Events.ACTION_BAR_MESSAGE.invoke(listener -> listener.onActionBarMessage(formatted, unformatted, ci));
        }

        else {
            Events.MESSAGE.invoke(listener -> listener.onMessage(formatted, unformatted, ci));
        }

        if (unformatted.trim().startsWith(CREATE_PROFILE_MESSAGE)) {
            Events.CREATE_BINGO_PROFILE.invoke(listener -> listener.onCreateBingoProfile());
        }
    }
}