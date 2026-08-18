package io.github.lilfroggy.bingohelper.guide.step;

public interface Property {
    void register(Step step);
    void unregister(Step step);

    default void register() {
        register(null);
    }

    default void unregister() {
        unregister(null);
    }
}