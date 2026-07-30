package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.item.HasList;

import net.minecraft.client.player.LocalPlayer;

public class HasStep extends Step implements ClientTickEndEvent {

    public HasList items;

    @Override
    public String locallyFormatted() {
        String formatted = instruction;
        int i = 1;

        for (var entry : items.entrySet()) {
            String placeholder = "%" + (i++) + "%";
            var info = entry.getValue();

            if (info.done()) formatted = formatted.replace(placeholder, "&a(✔)");
            else formatted = formatted.replace(placeholder, "(" + info.count() + "/" + info.target() + ")");
        }
        return formatted;
    }

    @Override
    public void onInit() {
        // Nothing to reset
    }

    @Override
    public void onReset() {
        items.reset();
    }

    @Override
    protected void onActivate() {
        Events.CLIENT_TICK_END.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CLIENT_TICK_END.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (!(CLIENT.player instanceof LocalPlayer player)) return;
        if (player.tickCount < 20) return;
        if (!items.update()) return;
        onProgress();
        if (items.allDone()) complete();
    }
}