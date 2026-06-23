package io.github.lilfroggy.bingohelper.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import io.github.lilfroggy.bingohelper.BingoHelper;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.guide.ActiveSteps;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.guide.step.Step;

public class KeyBindings {
    public static final KeyMapping.Category BINGO_HELPER_CATEGORY = KeyMapping.Category.register(BingoHelper.id("main"));
    
    static {
        Events.CLIENT_TICK_END.register(KeyBindings::onClientTickEnd);
    }
    
    public static KeyMapping BINGO_GUIDE_ACTION;
    
    public static void init() {
        BINGO_GUIDE_ACTION = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.bingohelper.bingo_guide_action",
            GLFW.GLFW_KEY_F,
            BINGO_HELPER_CATEGORY
        ));
    }
    
    public static void onClientTickEnd(int tick) {
        if (!Skyblock.inBingo()) return;
        if (!Config.guide) return;
        if (Guide.isCompleted()) return;

        Step blockingStep = ActiveSteps.getBlockingStepWithCommand();
        if (blockingStep == null) return;

        String command = blockingStep.command.replaceAll("%visitIsland%", Config.visitIsland);
        
        if (command.isEmpty() || !command.startsWith("/")) return;
        String keybindName = BINGO_GUIDE_ACTION.getTranslatedKeyMessage().getString();
        ChatLib.showTitle("", "§b" + command + " §7(§ePress " + keybindName + "§7)", 0, 2, 0);

        if (BINGO_GUIDE_ACTION.consumeClick()) {
            ChatLib.command(command);
        }
    }
} 