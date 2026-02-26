package io.github.lilfroggy.bingohelper.util;

import net.minecraft.client.MinecraftClient;

public class ClipboardUtils {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static String getClipboard() {
        return CLIENT.keyboard.getClipboard();
    }

    public static void setClipboard(String string) {
        if (string == null) return;
        CLIENT.keyboard.setClipboard(string);
    }
}