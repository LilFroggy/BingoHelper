package io.github.lilfroggy.bingohelper.events;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;

public class GuiCloseEventBus {
    public interface GuiCloseListener {
        void onGuiClose(Screen screen);
    }

    private static final EventBus<GuiCloseListener> BUS = new EventBus<>();

    static {
        // Register for all screens via AFTER_INIT to catch when they're removed
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.remove(screen).register((removedScreen) -> {
                for (GuiCloseListener listener : BUS.getListeners()) {
                    listener.onGuiClose(removedScreen);
                }
            });
        });
    }

    public static void register(GuiCloseListener listener) {
        BUS.register(listener);
    }

    public static void unregister(GuiCloseListener listener) {
        BUS.unregister(listener);
    }
}