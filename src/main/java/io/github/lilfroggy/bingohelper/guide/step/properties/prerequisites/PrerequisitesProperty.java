package io.github.lilfroggy.bingohelper.guide.step.properties.prerequisites;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import io.github.lilfroggy.bingohelper.guide.ActiveSteps;
import io.github.lilfroggy.bingohelper.guide.GuideSaver;
import io.github.lilfroggy.bingohelper.guide.step.Step;

public class PrerequisitesProperty {
    private static final String PREREQUISITE_DISPLAY_FORMAT = "&f%s\n&f- %s&f";

    private Step current;
    private Step parent;
    public int index;

    public Step[] steps;

    public void register(Step parent) {
        this.parent = parent;
        setCurrent();
    }

    public void unregister() {
        for (Step step : steps) {
            step.deactivate();
        }
    }

    public void previous() {
        current.deactivate();
        setIndex(index - 1);
        setCurrent();
    }

    public void next() {
        current.deactivate();
        parent.lastProgressMs = System.currentTimeMillis();
        setIndex(index + 1);
        setCurrent();
    }

    public void reset() {
        index = 0;
    }

    public void setIndex(int index) {
        if (index < 0) index = 0;
        if (index >= steps.length) index = steps.length;
        this.index = index;
        GuideSaver.saveUserProgress();
    }

    private void setCurrent() {
        current = current();
        current.parent = parent;
        current.reset();
        current.activate();
    }

    private Step current() {
        return index >= steps.length ? parent : steps[index];
    }

    public String instruction() {
        if (ActiveSteps.priorityAmount() <= 1) {
            return current.globallyFormatted();
        } else if (current == parent) {
            return parent.globallyFormatted();
        } else {
            return PREREQUISITE_DISPLAY_FORMAT.formatted(
                parent.globallyFormatted(),
                current.globallyFormatted().replaceAll("\n", "\n&f- ")
            );
        }
    }

    @Nullable
    public String command() {
        return current != null ? current.command : null;
    }

    @Override
    public String toString() {
        return "PrerequisitesProperty{" +
                "index=" + index +
                ", steps=" + Arrays.toString(steps) +
                '}';
    }
}