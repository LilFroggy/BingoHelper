package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.MessageEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CakeStep extends Step implements MessageEvent {
    
    private static int totalCakes;
    transient public Set<String> eaten;

    @Override
    public String formattedInstruction() {
        return instruction
                .replaceAll("%cakes%", "(" + eaten.size() + "/" + totalCakes + ")");
    }

    @Override
    public void onInit() {
        totalCakes = 20;
        eaten = new HashSet<>();
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

        if (!eaten.add(cake)) return;

        if (eaten.size() >= totalCakes) complete();
    }
}