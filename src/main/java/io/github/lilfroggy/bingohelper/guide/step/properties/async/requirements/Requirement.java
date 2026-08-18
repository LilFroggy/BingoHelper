package io.github.lilfroggy.bingohelper.guide.step.properties.async.requirements;

public interface Requirement {
    void register(Runnable onChange);
    void unregister();
    boolean isMet();
}