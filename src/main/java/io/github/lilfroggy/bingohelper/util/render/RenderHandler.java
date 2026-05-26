package io.github.lilfroggy.bingohelper.util.render;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class RenderHandler<T> {

    private final List<T> listeners = new CopyOnWriteArrayList<>();

    public void register(T listener) {
        listeners.add(listener);
    }

    public void unregister(T listener) {
        listeners.remove(listener);
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