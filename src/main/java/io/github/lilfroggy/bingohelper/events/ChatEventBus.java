package io.github.lilfroggy.bingohelper.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ChatEventBus {
    public interface GameMessageListener {
        void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci);
    }

    private static final EventBus<GameMessageListener> BUS = new EventBus<>();

    public static void register(GameMessageListener listener) {
        BUS.register(listener);
    }

    public static void unregister(GameMessageListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        for (GameMessageListener listener : BUS.getListeners()) {
            listener.onGameMessage(formattedMsg, unformattedMsg, ci);
        }
    }
}