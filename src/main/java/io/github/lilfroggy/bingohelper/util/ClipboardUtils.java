package io.github.lilfroggy.bingohelper.util;

import net.minecraft.client.MinecraftClient;

public class ClipboardUtils {
    public static String getClipboard() {
        return MinecraftClient.getInstance().keyboard.getClipboard();
    }

    public static void setClipboard(String string) {
        if (string == null) return;
        MinecraftClient.getInstance().keyboard.setClipboard(string);
    }
}