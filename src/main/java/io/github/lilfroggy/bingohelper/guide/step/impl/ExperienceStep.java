package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import net.minecraft.client.player.LocalPlayer;

public class ExperienceStep extends Step implements ClientTickEndEvent {

    public int level;

    @Override
    public String locallyFormatted() {
        if (!(CLIENT.player instanceof LocalPlayer player)) return "(0/" + level + ")";

        return instruction
        .replaceAll("%level%", "(" + player.experienceLevel + "/" + level + ")");
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
        Events.CLIENT_TICK_END.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CLIENT_TICK_END.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (!(CLIENT.player instanceof LocalPlayer player)) return;
        if (player.experienceLevel >= level) complete();
    }

}