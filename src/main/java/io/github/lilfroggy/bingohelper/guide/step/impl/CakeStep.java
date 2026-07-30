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
    private static final Pattern CAKE_PATTERN = Pattern.compile("^(?:Big )?Yum! You (?:gain|refresh) \\+(?:\\d+). (?<cake>.*) for 48 hours!$");

    private static int totalCakes = 20;
    transient public Set<String> eaten = new HashSet<>();

    @Override
    public String locallyFormatted() {
        return instruction
                .replaceAll("%cakes%", "(" + eaten.size() + "/" + totalCakes + ")");
    }

    @Override
    public void onInit() {

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

    @Override
    public void onMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        Matcher matcher = CAKE_PATTERN.matcher(unformattedMsg);
        if (!matcher.matches()) return;
        String cake = matcher.group("cake");
        if (!eaten.add(cake)) return;
        if (eaten.size() >= totalCakes) complete();
        onProgress();
    }
}