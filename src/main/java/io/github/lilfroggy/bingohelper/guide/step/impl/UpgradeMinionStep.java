package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClickSlotEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.ItemUtils;
import io.github.lilfroggy.bingohelper.util.PlayerRank;
import io.github.lilfroggy.bingohelper.util.ScreenUtils;
import io.github.lilfroggy.bingohelper.util.SupercraftUtils;
import io.github.lilfroggy.bingohelper.util.ScreenUtils.ScreenSlots;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class UpgradeMinionStep extends Step implements ClientTickEndEvent, ClickSlotEvent, RenderScreenEvent {

    // Provided from step

    public String minionId; // COBBLESTONE_GENERATOR_1 -> COBBLESTONE
    public int fromLevel;
    public int toLevel;

    // internal

    private String desiredBaseId;
    private int desiredLevel;
    private String desiredId;
    private String finalId;

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
        // Do nothing
    }

    @Override
    protected void onActivate() {
        desiredBaseId = minionId + "_GENERATOR_"; // COBBLESTONE_GENERATOR_
        finalId = desiredBaseId + toLevel;
        setDesiredLevel(fromLevel + 1);

        Events.CLIENT_TICK_END.register(this);
        Events.CLICK_SLOT.register(this);
        Events.RENDER_SCREEN.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CLIENT_TICK_END.unregister(this);
        Events.CLICK_SLOT.unregister(this);
        Events.RENDER_SCREEN.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (ItemUtils.getCount(finalId) > 0) complete();
    }

    @Override
    public void onClickSlot(Slot slot, int slotIndex, int button, ContainerInput actionType, CallbackInfo ci) {
        if (!SupercraftUtils.craftedDesiredItem(ScreenUtils.slots.CONTAINER, desiredId, slotIndex)) setDesiredLevel(desiredLevel + 1);
    }

    @Override
    public void onRenderScreen(GuiGraphicsExtractor graphics, Screen screen, String title, ScreenSlots slots) {
        SupercraftUtils.highlightSlot(graphics, slots.CONTAINER, desiredId);
    }

    private void setDesiredLevel(int level) {
        desiredLevel = level;
        desiredId = desiredBaseId + desiredLevel;
        command = PlayerRank.canSupercraft() ? "/viewrecipe " + desiredId : "/craft";
    }
}