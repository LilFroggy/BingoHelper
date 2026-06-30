package io.github.lilfroggy.bingohelper.guide.step.properties.async;

import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.guide.step.Step;

public class AsyncProperty {
    public Integer effectiveIndex;
    public AsyncRequirements requirements;

    public void register(Step step) {
        if (effectiveIndex == null) effectiveIndex = Integer.MAX_VALUE;
        if (requirements != null) requirements.register(step);
    }

    public void unregister() {
        if (requirements != null) requirements.unregister();
    }

    public boolean isBlocking() {
        return Guide.index() >= effectiveIndex();
    }

    public int effectiveIndex() {
        return effectiveIndex == null ? Integer.MAX_VALUE : effectiveIndex;
    }

    public boolean hasRequirements() {
        return requirements != null;
    }

    public boolean meetsRequirements() {
        return requirements == null || requirements.areMet();
    }

    public boolean isHidden() {
        return !isBlocking() && !meetsRequirements();
    }
}