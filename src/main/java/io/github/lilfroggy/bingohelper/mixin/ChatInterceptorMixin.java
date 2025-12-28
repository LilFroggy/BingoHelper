package io.github.lilfroggy.bingohelper.mixin;

import io.github.lilfroggy.bingohelper.events.ActionBarEventBus;
import io.github.lilfroggy.bingohelper.events.ChatEventBus;
import io.github.lilfroggy.bingohelper.events.CreateBingoProfileEventBus;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatInterceptorMixin {

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (!Thread.currentThread().getName().equals("Render thread")) return;
        String formatted = packet.content().getString();
        String unformatted = ChatLib.removeFormatting(formatted);
        if (packet.overlay()) ActionBarEventBus.fire(formatted, unformatted, ci);
        else ChatEventBus.fire(formatted, unformatted, ci);
        if (unformatted.trim().startsWith("Welcome to SkyBlock Bingo!")) CreateBingoProfileEventBus.fire();
    }
}