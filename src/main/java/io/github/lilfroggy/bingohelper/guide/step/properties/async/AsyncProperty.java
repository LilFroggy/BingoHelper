package io.github.lilfroggy.bingohelper.guide.step.properties.async;

import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.guide.step.Step;

public class AsyncProperty {
    
    public Integer effectiveIn;
    public Integer effectiveAt = Integer.MAX_VALUE;
    public AsyncRequirements requirements;

    public void register(Step step) {
        if (effectiveIn != null) effectiveAt = step.index + effectiveIn;
        if (requirements != null) requirements.register(step);
    }

    public void unregister() {
        if (requirements != null) requirements.unregister();
    }

    public boolean isBlocking() {
        return Guide.index() >= effectiveAt();
    }

    public int effectiveAt() {
        return effectiveAt;
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