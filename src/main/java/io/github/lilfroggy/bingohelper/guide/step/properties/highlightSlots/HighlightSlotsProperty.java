package io.github.lilfroggy.bingohelper.guide.step.properties.highlightSlots;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.util.ScreenUtils.ScreenSlots;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.util.slot.SlotPredicate;

public class HighlightSlotsProperty extends SlotPredicate implements RenderScreenEvent {

    public void register() {
        super.register();
        Events.RENDER_SCREEN.register(this);
    }

    public void unregister() {
        Events.RENDER_SCREEN.unregister(this);
        super.unregister();
    }

    @Override
    public void onRenderScreen(GuiGraphicsExtractor graphics, Screen screen, String title, ScreenSlots slots) {
        getMatches().forEach(slot -> {
            RenderLib.highlightSlot(graphics, slot, highlightColor());
        });
    }

    @Override
    public String toString() {
        return "HighlightSlotsProperty{" +
                "matches=" + getMatches().size() +
                ", highlightColor=" + highlightColor() +
                '}';
    }
}