package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface RenderHudEvent {
    void onRenderHud(GuiGraphicsExtractor graphics, DeltaTracker tickDelta);
}