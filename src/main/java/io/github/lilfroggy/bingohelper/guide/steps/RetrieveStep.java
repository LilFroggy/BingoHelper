package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.events.SlotRenderEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class RetrieveStep extends Step implements
        ClientTickEventBus.ClientTickListener,
        SlotRenderEventBus.SlotRenderListener {

    public List<String> items;

    @Override
    public String additionalInstructionFormatting() {
        return instruction;
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        ClientTickEventBus.register(this);
        SlotRenderEventBus.register(this);
    }

    @Override
    protected void onDeactivate() {
        ClientTickEventBus.unregister(this);
        SlotRenderEventBus.unregister(this);
    }

    @Override
    public void onClientTick(int tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null || mc.player.age < 20) return;

        // Check if we have a container open
        if (mc.player.currentScreenHandler == null) return;

        // Check if player is in an Ender Chest
        if (mc.currentScreen == null || 
            mc.currentScreen.getTitle() == null || 
            !mc.currentScreen.getTitle().getString().contains("Ender Chest")) {
            return; // Wait until player opens Ender Chest
        }

        boolean hasItems = false;

        // Check container items (exclude player inventory slots which are the last 36 slots)
        for (int i = 0; i < mc.player.currentScreenHandler.getStacks().size() - 36; i++) {
            ItemStack item = mc.player.currentScreenHandler.getStacks().get(i);
            if (item.isEmpty()) continue;

            String itemId = Skyblock.getID(item);
            if (itemId == null || !items.contains(itemId)) continue;

            hasItems = true;
        }

        if (!hasItems) Guide.advance();
    }

    @Override
    public void onSlotRender(DrawContext context, Slot slot) {
        if (slot.inventory instanceof PlayerInventory) return;
        ItemStack item = slot.getStack();
        if (item.isEmpty()) return;
        String itemId = Skyblock.getID(item);
        if (itemId == null || !items.contains(itemId)) return;
        RenderLib.highlightSlot(context, slot, RenderLib.MINECRAFT_AQUA);
    }
}