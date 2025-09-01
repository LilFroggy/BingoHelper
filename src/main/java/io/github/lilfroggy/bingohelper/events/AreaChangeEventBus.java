package io.github.lilfroggy.bingohelper.events;

public class AreaChangeEventBus {
    public interface AreaChangeListener {
        void onAreaChange(String newArea, String oldArea);
    }

    private static final EventBus<AreaChangeListener> BUS = new EventBus<>();

    public static void register(AreaChangeListener listener) {
        BUS.register(listener);
    }

    public static void unregister(AreaChangeListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(String newArea, String oldArea) {
        for (AreaChangeListener listener : BUS.getListeners()) {
            listener.onAreaChange(newArea, oldArea);
        }
    }
}