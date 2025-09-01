package io.github.lilfroggy.bingohelper.events;

public class SubAreaChangeEventBus {
    public interface SubAreaChangeListener {
        void onSubAreaChange(String newSubArea, String oldSubArea);
    }

    private static final EventBus<SubAreaChangeListener> BUS = new EventBus<>();

    public static void register(SubAreaChangeListener listener) {
        BUS.register(listener);
    }

    public static void unregister(SubAreaChangeListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(String newSubArea, String oldSubArea) {
        for (SubAreaChangeListener listener : BUS.getListeners()) {
            listener.onSubAreaChange(newSubArea, oldSubArea);
        }
    }
}