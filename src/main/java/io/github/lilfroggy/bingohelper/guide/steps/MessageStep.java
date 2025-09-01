package io.github.lilfroggy.bingohelper.guide.steps;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lilfroggy.bingohelper.events.ChatEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;

import net.minecraft.client.MinecraftClient;

public class MessageStep extends Step implements ChatEventBus.GameMessageListener {
    public String criteria;

    @Override
    public String additionalInstructionFormatting() {
        return instruction;
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        ChatEventBus.register(this);
    }

    @Override
    protected void onDeactivate() {
        ChatEventBus.unregister(this);
    }

    @Override
    public void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) return;

        if(unformattedMsg.trim().startsWith(criteria.trim())) Guide.advance();
    }
}