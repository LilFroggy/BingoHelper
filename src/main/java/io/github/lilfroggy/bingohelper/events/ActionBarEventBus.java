package io.github.lilfroggy.bingohelper.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ActionBarEventBus {

    public interface ActionBarMessageListener {
        void onActionBarMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci);
    }

    private static final EventBus<ActionBarMessageListener> BUS = new EventBus<>();

    public static void register(ActionBarMessageListener listener) {
        BUS.register(listener);
    }

    public static void unregister(ActionBarMessageListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        for (ActionBarMessageListener listener : BUS.getListeners()) {
            listener.onActionBarMessage(formattedMsg, unformattedMsg, ci);
        }
    }
}