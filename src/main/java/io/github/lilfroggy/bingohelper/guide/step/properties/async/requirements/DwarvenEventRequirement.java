package io.github.lilfroggy.bingohelper.guide.step.properties.async.requirements;

import io.github.lilfroggy.bingohelper.util.dwarvenEvents.DwarvenEvents;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.Type;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.interfaces.DwarvenEventEndEvent;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.interfaces.DwarvenEventStartEvent;

public class DwarvenEventRequirement implements Requirement, DwarvenEventStartEvent, DwarvenEventEndEvent {

    private Runnable onChange;
    private String dwarvenEvent;

    public DwarvenEventRequirement(String dwarvenEvent) {
        this.dwarvenEvent = dwarvenEvent;
    }

    @Override
    public void register(Runnable onChange) {
        if (dwarvenEvent == null) return;
        this.onChange = onChange;
        DwarvenEvents.ON_START.register(this);
        DwarvenEvents.ON_END.register(this);
    }

    @Override
    public void unregister() {
        if (dwarvenEvent == null) return;
        DwarvenEvents.ON_START.unregister(this);
        DwarvenEvents.ON_END.unregister(this);
    }

    @Override
    public boolean isMet() {
        return dwarvenEvent == null || DwarvenEvents.isActive(dwarvenEvent);
    }

    @Override
    public void onDwarvenEventEnd(String name) {
        if (!isEvent(name)) return;
        onChange.run();
    }

    @Override
    public void onDwarvenEventStart(String name) {
        if (!isEvent(name)) return;
        onChange.run();
    }

    private boolean isEvent(String event) {
        return Type.of(dwarvenEvent) == Type.of(event);
    }

    @Override
    public String toString() {
        return "DwarvenEventRequirement{" +
                "dwarvenEvent='" + dwarvenEvent + '\'' +
                ", isMet=" + isMet() +
                '}';
    }
}