package io.github.lilfroggy.bingohelper.guide.step.properties.highlightSlots;

import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.util.ScreenUtils.ScreenSlots;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.util.slot.SlotPredicate;

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
    public void onRenderScreen(GuiGraphicsExtractor graphics, Screen screen, String title, ScreenSlots slots) {
        super.getMatches().forEach(slot -> {
            RenderLib.highlightSlot(graphics, slot, super.highlightColor());
        });
    }
}