package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClickSlotEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.util.slot.SlotPredicate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

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
    public String formattedInstruction() {
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
    public void onClickSlot(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (predicate.matches(slot)) complete();
    }

    @Override
    public void onRenderScreen(DrawContext context, Screen screen, String title, DefaultedList<Slot> slots) {
        if (guiName != null && !title.contains(guiName)) return;

        for (Slot slot : slots) {
            if (!predicate.matches(slot)) continue;
            RenderLib.highlightSlot(context, slot, RenderLib.MINECRAFT_GREEN);
        }
    }
}