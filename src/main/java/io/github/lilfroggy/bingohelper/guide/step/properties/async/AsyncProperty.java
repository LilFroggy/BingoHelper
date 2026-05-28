package io.github.lilfroggy.bingohelper.guide.step.properties.async;

import io.github.lilfroggy.bingohelper.guide.Guide;

public class AsyncProperty {
    public Integer effectiveIndex;
    public AsyncRequirements requirements;

    public void init() {
        if (effectiveIndex == null) effectiveIndex = Integer.MAX_VALUE;
    }

    public void register() {
        if (requirements != null) requirements.register();
    }

    public void unregister() {
        if (requirements != null) requirements.unregister();
    }

    public boolean isBlocking() {
        return Guide.index() >= effectiveIndex;
    }

    public int effectiveIndex() {
        return effectiveIndex;
    }

    public boolean meetsRequirements() {
        return requirements == null || requirements.areMet();
    }

    public boolean isHidden() {
        return !isBlocking() && !meetsRequirements();
    }
}