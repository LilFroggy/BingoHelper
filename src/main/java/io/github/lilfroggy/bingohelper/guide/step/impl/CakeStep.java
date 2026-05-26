package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.MessageEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CakeStep extends Step implements MessageEvent {
    
    public List<String> eaten;

    @Override
    public String formattedInstruction() {
        return instruction
                .replaceAll("%cakes%", "(" + eaten.size() + "/16)");
    }

    @Override
    public void onInit() {
        // Nothing to reset
    }

    @Override
    public void onReset() {
        eaten.clear();
    }

    @Override
    protected void onActivate() {
        Events.MESSAGE.register(this);
    }


    @Override
    protected void onDeactivate() {
        Events.MESSAGE.unregister(this);
    }

    private static final Pattern CAKE_REGEX = Pattern.compile("^(Big )?Yum! You (gain|refresh) \\+(\\d+). (.*) for 48 hours!$");

    @Override
    public void onMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        Matcher matcher = CAKE_REGEX.matcher(unformattedMsg);
        if (!matcher.matches()) return;
        String cake = matcher.group(4);

        if (!eaten.contains(cake)) eaten.add(cake);

        if (eaten.size() >= 16) complete();
    }
}