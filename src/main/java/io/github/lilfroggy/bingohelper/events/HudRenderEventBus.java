package io.github.lilfroggy.bingohelper.events;

import io.github.lilfroggy.bingohelper.BingoHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudRenderEventBus {
    public interface HudRenderListener {
        void onHudRender(DrawContext drawContext, RenderTickCounter tickDelta);
    }

    private static final EventBus<HudRenderListener> BUS = new EventBus<>();

    static {
        HudElementRegistry.addLast(BingoHelper.id("hud"), (context, tickCounter) -> {
            for (HudRenderListener listener : BUS.getListeners()) {
                listener.onHudRender(context, tickCounter);
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