package io.github.lilfroggy.bingohelper.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

import org.lwjgl.glfw.GLFW;

import io.github.lilfroggy.bingohelper.BingoHelper;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;

public class KeyBindings {
    public static final KeyBinding.Category BINGO_HELPER_CATEGORY = KeyBinding.Category.create(BingoHelper.id("main"));
    
    static {
        ClientTickEventBus.register(KeyBindings::onClientTick);
    }
    
    public static KeyBinding BINGO_GUIDE_ACTION;
    
    public static void init() {
        BINGO_GUIDE_ACTION = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bingohelper.bingo_guide_action",
            GLFW.GLFW_KEY_F,
            BINGO_HELPER_CATEGORY
        ));
    }
    
    public static void onClientTick(int tick) {
        if (!Skyblock.inBingo()) return;
        if (!Config.guide) return;
        if (Guide.completed) return;
        if (Guide.currentStep == null) return;
        if (Guide.currentStep.command == null) return;
        
        String commandName = Guide.currentStep.command.replaceAll("%visitIsland%", Config.visitIsland);
        if (commandName == null || commandName.isEmpty() || !commandName.startsWith("/")) return;
        String keybindName = BINGO_GUIDE_ACTION.getBoundKeyLocalizedText().getString();
        ChatLib.showTitle("", "§b" + commandName + " §7(§ePress " + keybindName + "§7)", 0, 1, 0);

        if (BINGO_GUIDE_ACTION.wasPressed()) {
            ChatLib.command(commandName, false);
        }
    }
} 