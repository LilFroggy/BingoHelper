package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import net.minecraft.client.network.ClientPlayerEntity;

public class ExperienceStep extends Step implements ClientTickEndEvent {

    public int level;

    @Override
    public String formattedInstruction() {
        if (!(CLIENT.player instanceof ClientPlayerEntity player)) return "(0/" + level + ")";

        int playerLevel = player.experienceLevel;

        return instruction
        .replaceAll("%level%", "(" + playerLevel + "/" + level + ")");
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
        if (!(CLIENT.player instanceof ClientPlayerEntity player)) return;

        int playerLevel = player.experienceLevel;
    
        if (playerLevel >= level) complete();
    }

}