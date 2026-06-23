package io.github.lilfroggy.bingohelper.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;

@Mixin(ChatComponent.class)
public class ChatHudMixin {
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"))
    private void onAddMessage(Component message, CallbackInfo ci) {
        String formatted = message.getString();
        String unformatted = ChatLib.removeFormatting(formatted);

        Events.CLIENT_MESSAGE.invoke(listener -> listener.onClientMessage(formatted, unformatted, ci));
    }
}