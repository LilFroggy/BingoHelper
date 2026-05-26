package io.github.lilfroggy.bingohelper.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.util.Logger;

public class EventHandler<T> {

    private final List<T> listeners = new CopyOnWriteArrayList<>();

    public void register(T listener) {
        if (listeners.contains(listener)) {
            Logger.info("Listener has already been registered once: " + listener.getClass().getSimpleName(), !Config.debug);
            return;
        }
        
        listeners.add(listener);
        Logger.info("Registered listener: " + listener.getClass().getSimpleName(), !Config.debug);
    }

    public void unregister(T listener) {
        if (!listeners.remove(listener)) {
            Logger.info("Listener was not registered in the first place: " + listener.getClass().getSimpleName(), !Config.debug);
            return;
        }
        
        Logger.info("Unregistered listener: " + listener.getClass().getSimpleName(), !Config.debug);
    }

    public void invoke(Consumer<T> action) {
        if (listeners.isEmpty()) return;
        for (T listener : listeners) {
            action.accept(listener);
        }
    }

    public List<T> getListeners() {
        return listeners;
    }
}