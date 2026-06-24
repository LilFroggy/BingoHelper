package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.inventory.Slot;

public interface RenderSlotEvent {
    void onRenderSlot(GuiGraphicsExtractor graphics, Slot slot);
}