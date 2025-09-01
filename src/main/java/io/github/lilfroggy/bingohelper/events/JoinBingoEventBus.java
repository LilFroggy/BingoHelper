package io.github.lilfroggy.bingohelper.events;

public class JoinBingoEventBus {
    public interface JoinBingoListener {
        void onJoinBingo();
    }

    private static final EventBus<JoinBingoListener> BUS = new EventBus<>();

    public static void register(JoinBingoListener listener) {
        BUS.register(listener);
    }

    public static void unregister(JoinBingoListener listener) {
        BUS.unregister(listener);
    }

    public static void fire() {
        for (JoinBingoListener listener : BUS.getListeners()) {
            listener.onJoinBingo();
        }
    }
}