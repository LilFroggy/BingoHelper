package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import net.minecraft.client.MinecraftClient;

public class ExperienceStep extends Step implements
        ClientTickEventBus.ClientTickListener {

    public int level;

    @Override
    public String additionalInstructionFormatting() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return "(0/" + level + ")";

        int playerLevel = mc.player.experienceLevel;

        return instruction
        .replaceAll("%level%", "(" + playerLevel + "/" + level + ")");
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        ClientTickEventBus.register(this);
    }

    @Override
    protected void onDeactivate() {
        ClientTickEventBus.unregister(this);
    }

    @Override
    public void onClientTick(int tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        int playerLevel = mc.player.experienceLevel;
    
        if (playerLevel >= level) Guide.advance();
    }

}