package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.Slot;

public interface RenderSlotEvent {
    void onRenderSlot(GuiGraphics graphics, Slot slot);
}