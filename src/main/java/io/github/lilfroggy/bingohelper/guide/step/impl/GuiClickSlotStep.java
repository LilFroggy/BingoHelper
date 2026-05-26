package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClickSlotEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.ScreenUtils;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class GuiClickSlotStep extends Step implements ClickSlotEvent, RenderScreenEvent {

    public String guiName;
    public int slotIndex;

    @Override
    public String formattedInstruction() {
        return instruction;
    }

    @Override
    public void onInit() {
        // Nothing to reset
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        Events.CLICK_SLOT.register(this);
        Events.RENDER_SCREEN.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CLICK_SLOT.unregister(this);
        Events.RENDER_SCREEN.unregister(this);
    }

    @Override
    public void onClickSlot(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (!ScreenUtils.getTitle().equals(guiName)) return;
        if (slotId == slotIndex) complete();
    }

    @Override
    public void onRenderScreen(DrawContext context, Screen screen, String title, DefaultedList<Slot> slots) {
        if (!title.contains(guiName)) return;
        RenderLib.highlightSlot(context, slots.get(slotIndex), RenderLib.MINECRAFT_GREEN);
    }
}