package io.github.lilfroggy.bingohelper.events;

public class CreateBingoProfileEventBus {
    public interface CreateBingoProfileListener {
        void onCreateBingoProfile();
    }
    private static final EventBus<CreateBingoProfileListener> BUS = new EventBus<>();

    public static void register(CreateBingoProfileListener listener) {
        BUS.register(listener);
    }

    public static void unregister(CreateBingoProfileListener listener) {
        BUS.unregister(listener);
    }

    public static void fire() {
        for (CreateBingoProfileListener listener : BUS.getListeners()) {
            listener.onCreateBingoProfile();
        }
    }
}