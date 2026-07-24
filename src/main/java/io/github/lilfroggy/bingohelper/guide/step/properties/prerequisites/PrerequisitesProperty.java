package io.github.lilfroggy.bingohelper.guide.step.properties.prerequisites;

import io.github.lilfroggy.bingohelper.guide.step.Step;

public class PrerequisitesProperty {
    private Step current;
    private Step parent;
    private int index;

    public Step[] steps;

    public void register(Step parent) {
        this.parent = parent;
        setCurrent(steps[index]);
    }

    public void unregister() {
        for (Step step : steps) {
            step.deactivate();
        }
    }

    public void previous() {
        current.deactivate();
        if (index <= 0) return;
        setCurrent(steps[--index]);
    }

    public void next() {
        current.deactivate();
        current = parent;
        if (index >= steps.length - 1) return;
        setCurrent(steps[++index]);
    }

    public void reset() {
        index = 0;
    }

    private void setCurrent(Step step) {
        step.parent = parent;
        step.reset();
        step.activate();
        current = step;
    }

    public String instruction() {
        return current.globallyFormatted();
    }
}