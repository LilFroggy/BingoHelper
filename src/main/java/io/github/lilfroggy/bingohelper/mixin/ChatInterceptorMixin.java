package io.github.lilfroggy.bingohelper.mixin;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatInterceptorMixin {
    private static final String CREATE_PROFILE_MESSAGE = "Welcome to SkyBlock Bingo!";

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) {
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