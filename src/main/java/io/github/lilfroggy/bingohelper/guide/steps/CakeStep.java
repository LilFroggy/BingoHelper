package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.ChatEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;

import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CakeStep extends Step implements ChatEventBus.GameMessageListener {
    public List<String> eaten;

    @Override
    public String additionalInstructionFormatting() {
        return instruction
                .replaceAll("%cakes%", "(" + eaten.size() + "/16)");
    }

    @Override
    public void onReset() {
        eaten.clear();
    }

    @Override
    protected void onActivate() {
        ChatEventBus.register(this);
    }


    @Override
    protected void onDeactivate() {
        ChatEventBus.unregister(this);
    }

    private static final Pattern CAKE_REGEX = Pattern.compile("^(Big )?Yum! You (gain|refresh) \\+(\\d+). (.*) for 48 hours!$");

    @Override
    public void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) return;

        Matcher matcher = CAKE_REGEX.matcher(unformattedMsg);
        if (!matcher.matches()) return;
        String cake = matcher.group(4);

        if (!eaten.contains(cake)) eaten.add(cake);

        if (eaten.size() >= 16) Guide.advance();
    }
}