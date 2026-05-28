package io.github.lilfroggy.bingohelper.guide.step.properties.highlightSlots;

import java.util.List;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.util.slot.SlotPredicate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class HighlightSlotsProperty extends SlotPredicate implements RenderScreenEvent {

    public HighlightSlotsProperty(String guiName, Integer slotIndex, String skyblockId, List<String> has, List<String> doesntHave, Boolean playerInv, String highlightColor) {
        super(guiName, slotIndex, skyblockId, has, doesntHave, playerInv, highlightColor);
    }

    public void register() {
        super.register();
        Events.RENDER_SCREEN.register(this);
    }

    public void unregister() {
        Events.RENDER_SCREEN.unregister(this);
        super.unregister();
    }

    @Override
    public void onRenderScreen(DrawContext context, Screen screen, String title, DefaultedList<Slot> slots) {
        super.getMatches().forEach(slot -> {
            RenderLib.highlightSlot(context, slot, super.highlightColor());
        });
    }
}