package io.github.lilfroggy.bingohelper.events;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;

public class SlotRenderEventBus {
    public interface SlotRenderListener {
        void onSlotRender(DrawContext context, Slot slot);
    }

    private static final EventBus<SlotRenderListener> BEFORE = new EventBus<>();
    private static final EventBus<SlotRenderListener> AFTER = new EventBus<>();

    public static void register(SlotRenderListener listener) {
        AFTER.register(listener);
    }

    public static void register(SlotRenderListener listener, boolean before) {
        if (before) BEFORE.register(listener);
        else register(listener);
    }

    public static void unregister(SlotRenderListener listener) {
        BEFORE.unregister(listener);
        AFTER.unregister(listener);
    }

    public static void fireBefore(DrawContext context, Slot slot) {
        for (SlotRenderListener listener : BEFORE.getListeners()) {
            listener.onSlotRender(context, slot);
        }
    }

    public static void fireAfter(DrawContext context, Slot slot) {
        for (SlotRenderListener listener : AFTER.getListeners()) {
            listener.onSlotRender(context, slot);
        }
    }
}