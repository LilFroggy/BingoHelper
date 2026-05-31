package io.github.lilfroggy.bingohelper.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class ScreenUtils {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static String getTitle() {
        return CLIENT.currentScreen != null ? CLIENT.currentScreen.getTitle().getString() : ""; 
    }

    public static DefaultedList<Slot> getSlots() {
        if (!(CLIENT.player instanceof ClientPlayerEntity player)) return DefaultedList.of();
        if (!(player.currentScreenHandler instanceof ScreenHandler handler)) return DefaultedList.of();
        return handler.slots;
    }

    public static ItemStack getCursorStack() {
        if (!(CLIENT.player instanceof ClientPlayerEntity player)) return ItemStack.EMPTY;
        if (!(player.currentScreenHandler instanceof ScreenHandler handler)) return ItemStack.EMPTY;
        return handler.getCursorStack();
    }
}