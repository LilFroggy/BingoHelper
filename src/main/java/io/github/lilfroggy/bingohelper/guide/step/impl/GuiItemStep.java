package io.github.lilfroggy.bingohelper.guide.step.impl;

import java.util.List;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.slot.SlotPredicate;

public class GuiItemStep extends Step implements ClientTickEndEvent {

    public String guiName;
    public Integer slotIndex;
    public String skyblockId;
    public List<String> has;
    public List<String> doesntHave;
    public Boolean playerInv;

    public SlotPredicate predicate;

    @Override
    public String formattedInstruction() {
        return instruction;
    }

    @Override
    public void onInit() {
        predicate = new SlotPredicate(guiName, slotIndex, skyblockId, has, doesntHave, playerInv, null);
    }

    @Override
    public void onReset() {
        // Do nothing
    }

    @Override
    protected void onActivate() {
        predicate.register();
        Events.CLIENT_TICK_END.register(this);
    }

    @Override
    protected void onDeactivate() {
        predicate.unregister();
        Events.CLIENT_TICK_END.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (predicate.hasMatch()) complete();
    }
}