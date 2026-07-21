package io.github.lilfroggy.bingohelper.events.interfaces;

import io.github.lilfroggy.bingohelper.util.ScreenUtils.ScreenSlots;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public interface RenderScreenEvent {
    void onRenderScreen(GuiGraphicsExtractor graphics, Screen screen, String title, ScreenSlots slots);
}