package io.github.lilfroggy.bingohelper.events;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class ScreenRenderEventBus {
    private static final EventBus<ScreenRenderListener> BUS = new EventBus<>();

    static {
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof HandledScreen<?>) {
                ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, tickDelta) -> {
                    for (ScreenRenderListener listener : BUS.getListeners()) {
                        try {
                            listener.onScreenRender(currentScreen, drawContext, mouseX, mouseY, tickDelta);
                        } catch (Exception e) {
                            System.err.println("Error in screen render listener: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public static void register(ScreenRenderListener listener) {
        BUS.register(listener);
    }

    public static void unregister(ScreenRenderListener listener) {
        BUS.unregister(listener);
    }

    @FunctionalInterface
    public interface ScreenRenderListener {
        /**
         * Called during screen rendering (after background, before tooltips)
         * @param screen The current screen being rendered
         * @param drawContext The drawing context for rendering
         * @param mouseX Mouse X position
         * @param mouseY Mouse Y position
         * @param tickDelta Partial tick time (0.0 to 1.0)
         */
        void onScreenRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta);
    }
}