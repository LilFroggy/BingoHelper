package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public interface RenderHudEvent {
    void onRenderHud(DrawContext drawContext, RenderTickCounter tickDelta);
}