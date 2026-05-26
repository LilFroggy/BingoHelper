package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;

public interface RenderSlotEvent {
    void onRenderSlot(DrawContext context, Slot slot);
}