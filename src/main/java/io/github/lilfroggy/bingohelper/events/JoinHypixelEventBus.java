package io.github.lilfroggy.bingohelper.events;

public class JoinHypixelEventBus {
    public interface JoinHypixelListener {
        void onJoinHypixel(boolean isAlpha);
    }
    
    private static final EventBus<JoinHypixelListener> BUS = new EventBus<>();

    public static void register(JoinHypixelListener listener) {
        BUS.register(listener);
    }
    
    public static void unregister(JoinHypixelListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(boolean isAlpha) {
        for (JoinHypixelListener listener : BUS.getListeners()) {
            listener.onJoinHypixel(isAlpha);
        }
    }
}