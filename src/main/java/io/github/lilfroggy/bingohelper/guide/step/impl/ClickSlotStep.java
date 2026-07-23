package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClickSlotEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.ScreenUtils.ScreenSlots;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.util.slot.SlotPredicate;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ClickSlotStep extends Step implements ClickSlotEvent, RenderScreenEvent {

    public String guiName;
    public Integer slotIndex;
    public String skyblockId;
    public List<String> has;
    public List<String> doesntHave;
    public Boolean playerInv;

    private SlotPredicate predicate;

    @Override
    public String locallyFormatted() {
        return instruction;
    }

    @Override
    public void onInit() {
        predicate = new SlotPredicate(guiName, slotIndex, skyblockId, has, doesntHave, playerInv, null);
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        predicate.register();
        Events.CLICK_SLOT.register(this);
        Events.RENDER_SCREEN.register(this);
    }

    @Override
    protected void onDeactivate() {
        predicate.unregister();
        Events.CLICK_SLOT.unregister(this);
        Events.RENDER_SCREEN.unregister(this);
    }

    @Override
    public void onClickSlot(Slot slot, int slotId, int button, ContainerInput actionType, CallbackInfo ci) {
        if (predicate.matches(slot)) complete();
    }

    @Override
    public void onRenderScreen(GuiGraphicsExtractor graphics, Screen screen, String title, ScreenSlots slots) {
        if (guiName != null && !title.contains(guiName)) return;

        for (Slot slot : slots.ALL) {
            if (!predicate.matches(slot)) continue;
            RenderLib.highlightSlot(graphics, slot, RenderLib.MINECRAFT_GREEN);
        }
    }
}