package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface RenderHudEvent {
    void onRenderHud(GuiGraphics graphics, DeltaTracker tickDelta);
}