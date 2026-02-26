package io.github.lilfroggy.bingohelper.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class ScreenRenderEventBus {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public interface ScreenRenderListener {
        void onScreenRender(DrawContext context, Screen screen, String title, DefaultedList<Slot> slots);
    }

    private static final EventBus<ScreenRenderListener> BUS = new EventBus<>();

    public static void register(ScreenRenderListener listener) {
        BUS.register(listener);
    }

    public static void unregister(ScreenRenderListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(DrawContext context) {
        if (BUS.getListeners().isEmpty()) return;
        
        Screen screen = CLIENT.currentScreen;
        if (screen == null || screen.getTitle() == null) return;
        String title = screen.getTitle().getString();

        if (CLIENT.player == null || CLIENT.player.currentScreenHandler == null) return;
        var slots = CLIENT.player.currentScreenHandler.slots;

        for (ScreenRenderListener listener : BUS.getListeners()) {
            listener.onScreenRender(context, screen, title, slots);
        }
    }
}