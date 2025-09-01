package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.SubAreaChangeEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Location;

public class SubAreaStep extends Step implements SubAreaChangeEventBus.SubAreaChangeListener {
    public String subArea;

    @Override
    public String additionalInstructionFormatting() {
        return instruction;
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        SubAreaChangeEventBus.register(this);

        // Need initial check
        if(subArea.equals(Location.SUB_AREA)) Guide.advance();
    }

    @Override
    protected void onDeactivate() {
        SubAreaChangeEventBus.unregister(this);
    }

    @Override
    public void onSubAreaChange(String newSubArea, String oldSubArea) {
        if(subArea.equals(newSubArea)) Guide.advance();
    }
}