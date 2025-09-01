package io.github.lilfroggy.bingohelper.events;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudRenderEventBus {

    public interface HudRenderListener {
        void onHudRender(DrawContext drawContext, RenderTickCounter tickDelta);
    }

    private static final EventBus<HudRenderListener> BUS = new EventBus<>();

    static {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            for (HudRenderListener listener : BUS.getListeners()) {
                listener.onHudRender(drawContext, tickDelta);
            }
        });
    }

    public static void register(HudRenderListener listener) {
        BUS.register(listener);
    }

    public static void unregister(HudRenderListener listener) {
        BUS.unregister(listener);
    }
}