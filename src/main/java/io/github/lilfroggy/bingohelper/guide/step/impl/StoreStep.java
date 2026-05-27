package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderSlotEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class StoreStep extends Step implements ClientTickEndEvent, RenderSlotEvent {

    public List<String> items;

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
        Events.CLIENT_TICK_END.register(this);
        Events.RENDER_SLOT.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CLIENT_TICK_END.unregister(this);
        Events.RENDER_SLOT.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (!(CLIENT.player instanceof ClientPlayerEntity player)) return;
        if (player.age < 20) return;

        var stacks = player.getInventory().getMainStacks();

        boolean hasItems = false;

        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;

            String id = Skyblock.getID(stack);
            if(id == null || !items.contains(id)) continue;

            hasItems = true;
        }

        if (!hasItems) complete();
    }

    @Override
    public void onRenderSlot(DrawContext context, Slot slot) {
        if (!(slot.inventory instanceof PlayerInventory)) return;
        ItemStack item = slot.getStack();
        if (item.isEmpty()) return;
        String itemId = Skyblock.getID(item);
        if (itemId == null || !items.contains(itemId)) return;

        RenderLib.highlightSlot(context, slot, RenderLib.MINECRAFT_AQUA);
    }
}