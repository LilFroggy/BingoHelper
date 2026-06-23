package io.github.lilfroggy.bingohelper.util;

import net.minecraft.client.Minecraft;

public class ClipboardUtils {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static String getClipboard() {
        return CLIENT.keyboardHandler.getClipboard();
    }

    public static void setClipboard(String string) {
        if (string == null) return;
        CLIENT.keyboardHandler.setClipboard(string);
    }
}