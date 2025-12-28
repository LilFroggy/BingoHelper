package io.github.lilfroggy.bingohelper.events;

import java.util.ArrayList;

public class ScoreboardUpdateEventBus {
    public interface ScoreboardUpdateListener {
        void onScoreboardUpdate(ArrayList<String> lines);
    }

    private static final EventBus<ScoreboardUpdateListener> BUS = new EventBus<>();

    public static void register(ScoreboardUpdateListener listener) {
        BUS.register(listener);
    }

    public static void unregister(ScoreboardUpdateListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(ArrayList<String> lines) {
        for (ScoreboardUpdateListener listener : BUS.getListeners()) {
            listener.onScoreboardUpdate(lines);
        }
    }
}