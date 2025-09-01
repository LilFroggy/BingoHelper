package io.github.lilfroggy.bingohelper.events;

public class LeaveBingoEventBus {
    public interface LeaveBingoListener {
        void onLeaveBingo();
    }

    private static final EventBus<LeaveBingoListener> BUS = new EventBus<>();

    public static void register(LeaveBingoListener listener) {
        BUS.register(listener);
    }

    public static void unregister(LeaveBingoListener listener) {
        BUS.unregister(listener);
    }

    public static void fire() {
        for (LeaveBingoListener listener : BUS.getListeners()) {
            listener.onLeaveBingo();
        }
    }
}