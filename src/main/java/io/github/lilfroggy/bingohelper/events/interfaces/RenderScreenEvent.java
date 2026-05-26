package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public interface RenderScreenEvent {
    void onRenderScreen(DrawContext context, Screen screen, String title, DefaultedList<Slot> slots);
}