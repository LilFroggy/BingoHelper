package io.github.lilfroggy.bingohelper.guide.step.impl;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.MessageEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;

public class MessageStep extends Step implements MessageEvent {
    
    public String criteria;

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
        Events.MESSAGE.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.MESSAGE.unregister(this);
    }

    @Override
    public void onMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if(unformattedMsg.trim().startsWith(criteria.trim())) complete();
    }
}