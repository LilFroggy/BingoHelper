package io.github.lilfroggy.bingohelper.guide.step.impl;

import java.util.List;

import io.github.lilfroggy.bingohelper.data.MobTypes;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.UnlockMobTypeEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;

public class MobTypesStep extends Step implements UnlockMobTypeEvent {
    public List<String> mobTypes;

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
        Events.UNLOCK_MOB_TYPE.register(this);

        if (MobTypes.hasUnlocked(mobTypes)) complete(); // initial check
    }

    @Override
    protected void onDeactivate() {
        Events.UNLOCK_MOB_TYPE.unregister(this);
    }

    @Override
    public void onUnlockMobType() {
        if (MobTypes.hasUnlocked(mobTypes)) complete();
    }
    
}