package io.github.lilfroggy.bingohelper.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class ScreenUtils {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static String getTitle() {
        Screen screen = CLIENT.currentScreen;
        if (screen == null) return "";
        return screen.getTitle().getString();
    }

    public static DefaultedList<Slot> getSlots() {
        if (CLIENT.player == null || CLIENT.player.currentScreenHandler == null) return DefaultedList.of();
        return CLIENT.player.currentScreenHandler.slots;
    }
}