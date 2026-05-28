package io.github.lilfroggy.bingohelper.guide.step.impl;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClickSlotEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class UpgradeMinionStep extends Step implements ClientTickEndEvent, ClickSlotEvent, RenderScreenEvent {

    // Provided from step

    public String minionId; // COBBLESTONE_GENERATOR_1 -> COBBLESTONE
    public int fromLevel;
    public int toLevel;

    // internal

    private static final int SLOT_SUPERCRAFT = 32;
    private static final int SLOT_NEXT_PAGE = 53;
    private static final int SLOT_BACK_BUTTON = 48;

    private String desiredBaseId;
    private int desiredLevel;
    private String desiredId;
    private String finalId;
    private String desiredName;
    private boolean navigating = true;

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
        if (Skyblock.getItemCount(finalId) > 0) complete();
    }

    @Override
    public void onClickSlot(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        Screen screen = CLIENT.currentScreen;
        if (!(screen instanceof GenericContainerScreen)) return;

        ItemStack item = slot.getStack();
        if (item.isEmpty()) return;

        if (isNavigationScreen(screen)) handleNavigationClick(item);
        else if (isUpgradeScreen(screen)) handleUpgradeClick(item);
    }

    private void handleNavigationClick(ItemStack stack) {
        String id = Skyblock.getID(stack);
        if (id.isEmpty() || !id.equals(desiredId)) return;

        desiredName = stack.getName().getString();
        navigating = false;
    }

    private void handleUpgradeClick(ItemStack stack) {
        if (!stack.getName().getString().equals("Supercraft")) return;

        String lore = Skyblock.getLore(stack);
        if (lore == null || lore.contains("Missing ingredients!") || lore.contains("No inventory space!")) return;

        setDesiredLevel(desiredLevel + 1);
        navigating = true;
    }

    @Override
    public void onRenderScreen(DrawContext context, Screen screen, String title, DefaultedList<Slot> slots) {
        if (desiredLevel > toLevel) return;

        if (isNavigationScreen(screen)) highlightNavigationSlot(context, slots);
        else if (isUpgradeScreen(screen)) highlightUpgradeSlot(context, slots);
    }

    private void highlightNavigationSlot(DrawContext drawContext, DefaultedList<Slot> slots) {
        Slot slot = findSlotByItemId(slots, desiredId);
        if (slot != null) RenderLib.highlightSlot(drawContext, slot, RenderLib.MINECRAFT_GREEN);
    }

    private void highlightUpgradeSlot(DrawContext drawContext, DefaultedList<Slot> slots) {
        if (navigating) RenderLib.highlightSlot(drawContext, slots.get(SLOT_BACK_BUTTON), RenderLib.MINECRAFT_GREEN);
        else if (isMissingIngredients(slots) && isNextPage(slots)) RenderLib.highlightSlot(drawContext, slots.get(SLOT_NEXT_PAGE), RenderLib.MINECRAFT_GREEN);
        else if (isInventorySpace(slots)) RenderLib.highlightSlot(drawContext, slots.get(SLOT_SUPERCRAFT), RenderLib.MINECRAFT_GREEN);
    }

    private boolean isNavigationScreen(Screen screen) {
        return screen.getTitle().getString().endsWith("Minion Recipes");
    }
    
    private boolean isUpgradeScreen(Screen screen) {
        if (desiredName == null) return false;
        return screen.getTitle().getString().equals(desiredName + " Recipe");
    }

    private boolean isMissingIngredients(DefaultedList<Slot> slots) {
        if (slots.size() <= SLOT_SUPERCRAFT) return false;
        String lore = Skyblock.getLore(slots.get(SLOT_SUPERCRAFT).getStack());
        return lore != null && lore.contains("Missing ingredients!");
    }

    private boolean isNextPage(DefaultedList<Slot> slots) {
        if (slots.size() <= SLOT_NEXT_PAGE) return false;
        return slots.get(SLOT_NEXT_PAGE).getStack().getName().getString().equals("Next Recipe");
    }

    private boolean isInventorySpace(DefaultedList<Slot> slots) {
        if (slots.size() <= SLOT_SUPERCRAFT) return false;
        String lore = Skyblock.getLore(slots.get(SLOT_SUPERCRAFT).getStack());
        return lore != null && !lore.contains("No inventory space!");
    }

    @Nullable
    private Slot findSlotByItemId(DefaultedList<Slot> slots, String id) {
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot.inventory instanceof PlayerInventory) continue;
            if (id.equals(Skyblock.getID(slot.getStack()))) {
                return slot;
            }
        }
        return null;
    }

    private void setDesiredLevel(int level) {
        desiredLevel = level;
        if (desiredLevel > toLevel) return;
        desiredId = desiredBaseId + desiredLevel;
        command = "/viewrecipe " + desiredId;
    }
}