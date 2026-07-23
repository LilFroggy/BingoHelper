package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.SubAreaChangeEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Skyblock;

public class SubAreaStep extends Step implements SubAreaChangeEvent {

    public String subArea;

    @Override
    public String locallyFormatted() {
        return instruction;
    }

    @Override
    public void onInit() {
        // Nothing to reset
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        if (subArea.equals(Skyblock.subArea())) complete(); // Initial check
        Events.CHANGE_SUB_AREA.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CHANGE_SUB_AREA.unregister(this);
    }

    @Override
    public void onSubAreaChange(String newSubArea, String oldSubArea) {
        if (subArea.equals(newSubArea)) complete();
    }
}