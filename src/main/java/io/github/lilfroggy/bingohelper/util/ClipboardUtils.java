package io.github.lilfroggy.bingohelper.util;

import net.minecraft.client.util.Clipboard;

public class ClipboardUtils {
    public static String getClipboard() {
        try {
            return new Clipboard().getClipboard(0, (__, ___) -> {});
        } catch (Exception e) {
            return null;
        }
    }

    public static void setClipboard(String string) {
        try {
            new Clipboard().setClipboard(0, string);
        } catch (Exception e) {
            Logger.error("Error setting clipboard", e);
        }
    }
}