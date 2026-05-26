package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.AreaChangeEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Skyblock;

public class AreaStep extends Step implements AreaChangeEvent {

    public String area;

    @Override
    public String formattedInstruction() {
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
        Events.CHANGE_AREA.register(this);

        if (area.equals(Skyblock.area())) complete(); // initial check
    }

    @Override
    protected void onDeactivate() {
        Events.CHANGE_AREA.unregister(this);
    }

    @Override
    public void onAreaChange(String newArea, String oldArea) {
        if (area.equals(newArea)) complete();
    }
}