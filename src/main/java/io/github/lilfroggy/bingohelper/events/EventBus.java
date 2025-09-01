package io.github.lilfroggy.bingohelper.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus<T> {
    private final List<T> listeners = new CopyOnWriteArrayList<>();

    public void register(T listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
        System.out.println("Registered listener: " + listener.getClass().getSimpleName());
    }

    public void unregister(T listener) {
        if (!listeners.remove(listener)) return;
        System.out.println("Unregistered listener: " + listener.getClass().getSimpleName());
    }

    public List<T> getListeners() {
        return listeners;
    }
}