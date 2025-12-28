package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.AreaChangeEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Skyblock;

public class AreaStep extends Step implements AreaChangeEventBus.AreaChangeListener {
    public String area;

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
        AreaChangeEventBus.register(this);

        // Need initial check
        if(area.equals(Skyblock.area())) Guide.advance();
    }

    @Override
    protected void onDeactivate() {
        AreaChangeEventBus.unregister(this);
    }

    @Override
    public void onAreaChange(String newArea, String oldArea) {
        if(area.equals(newArea)) Guide.advance();
    }
}