package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.SubAreaChangeEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Skyblock;

public class SubAreaStep extends Step implements SubAreaChangeEvent {
    
    public String subArea;

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
        Events.CHANGE_SUB_AREA.register(this);

        if(subArea.equals(Skyblock.subArea())) complete(); // initial check
    }

    @Override
    protected void onDeactivate() {
        Events.CHANGE_SUB_AREA.unregister(this);
    }

    @Override
    public void onSubAreaChange(String newSubArea, String oldSubArea) {
        if (newSubArea.equals(subArea)) complete();
    }
}