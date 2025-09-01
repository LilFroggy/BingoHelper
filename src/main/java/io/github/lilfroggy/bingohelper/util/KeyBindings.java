package io.github.lilfroggy.bingohelper.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;

public class KeyBindings {
    
    static {
        ClientTickEventBus.register(KeyBindings::onClientTick);
    }

    // Define your keybinds
    public static KeyBinding BINGO_GUIDE_ACTION;
    
    public static void init() {
        // Register the toggle bingo guide keybind
        BINGO_GUIDE_ACTION = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bingohelper.bingo_guide_action", // Translation key
            InputUtil.Type.KEYSYM, // Type of input (keyboard key)
            GLFW.GLFW_KEY_F, // Default key (F key)
            "category.bingohelper.main" // Category in controls menu
        ));
        
        System.out.println("Registered BingoHelper keybinds");
    }
    
    public static void onClientTick(int tick) {
        if (!Skyblock.isInBingo) return;
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