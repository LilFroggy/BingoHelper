package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;

public interface RenderScreenEvent {
    void onRenderScreen(GuiGraphics graphics, Screen screen, String title, NonNullList<Slot> slots);
}