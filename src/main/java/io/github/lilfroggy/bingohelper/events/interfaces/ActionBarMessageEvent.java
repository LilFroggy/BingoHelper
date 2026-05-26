package io.github.lilfroggy.bingohelper.events.interfaces;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface ActionBarMessageEvent {
    void onActionBarMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci);
}