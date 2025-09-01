package io.github.lilfroggy.bingohelper.events;

public class SkillUpdateEventBus {

    public interface SkillUpdateListener {
        void onSkillUpdate(String skill, Integer previousLevel, Integer newLevel);
    }

    private static final EventBus<SkillUpdateListener> BUS = new EventBus<>();

    public static void register(SkillUpdateListener listener) {
        BUS.register(listener);
    }

    public static void unregister(SkillUpdateListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(String skill, Integer previousLevel, Integer newLevel) {
        for (SkillUpdateListener listener : BUS.getListeners()) {
            listener.onSkillUpdate(skill, previousLevel, newLevel);
        }
    }
} 