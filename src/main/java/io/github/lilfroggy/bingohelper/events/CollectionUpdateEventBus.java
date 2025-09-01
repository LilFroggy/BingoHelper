package io.github.lilfroggy.bingohelper.events;

public class CollectionUpdateEventBus {

    public interface CollectionUpdateListener {
        void onCollectionUpdate(String collection, Integer previousLevel, Integer newLevel);
    }

    private static final EventBus<CollectionUpdateListener> BUS = new EventBus<>();

    public static void register(CollectionUpdateListener listener) {
        BUS.register(listener);
    }

    public static void unregister(CollectionUpdateListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(String collection, Integer previousLevel, Integer newLevel) {
        for (CollectionUpdateListener listener : BUS.getListeners()) {
            listener.onCollectionUpdate(collection, previousLevel, newLevel);
        }
    }
}