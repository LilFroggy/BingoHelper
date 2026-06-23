package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderSlotEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SellStep extends Step implements ClientTickEndEvent, RenderSlotEvent {

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
        if (!(CLIENT.player instanceof LocalPlayer player)) return;
        if (player.tickCount < 20) return;

        var stacks = player.getInventory().getNonEquipmentItems();

        boolean hasItems = false;

        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;

            String id = Skyblock.getID(stack);
            if(id.isEmpty() || !items.contains(id)) continue;

            hasItems = true;
        }

        if (!hasItems) complete();
    }

    @Override
    public void onRenderSlot(GuiGraphics graphics, Slot slot) {
        if (!(slot.container instanceof Inventory)) return;
        ItemStack item = slot.getItem();
        if (item.isEmpty()) return;
        String itemId = Skyblock.getID(item);
        if (itemId.isEmpty() || !items.contains(itemId)) return;
        
        RenderLib.highlightSlot(graphics, slot, RenderLib.MINECRAFT_RED);
    }
}