package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClickSlotEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.CloseScreenEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.mixin.accessor.AbstractContainerScreenAccessor;
import io.github.lilfroggy.bingohelper.util.ItemUtils;
import io.github.lilfroggy.bingohelper.util.ScreenUtils.ScreenSlots;
import io.github.lilfroggy.bingohelper.util.item.ReforgeInfo;
import io.github.lilfroggy.bingohelper.util.item.ReforgeList;
import io.github.lilfroggy.bingohelper.util.render.Display;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReforgeStep extends Step implements RenderScreenEvent, ClientTickEndEvent, ClickSlotEvent, CloseScreenEvent {

    public ReforgeList items;

    private static final String REFORGE_SCREEN_TITLE = "Reforge Item";

    private static final int REFORGE_ITEM_SLOT_ID = 13;
    private static final int REFORGE_BUTTON_SLOT_ID = 22;

    @Override
    public String locallyFormatted() {
        return instruction;
    }

    @Override
    public void onInit() {
        // Nothing to reset
    }

    @Override
    public void onReset() {
        items.reset();
    }

    @Override
    protected void onActivate() {
        Events.CLIENT_TICK_END.register(this);
        Events.RENDER_SCREEN.register(this);
        Events.CLICK_SLOT.register(this);
        Events.CLOSE_SCREEN.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CLIENT_TICK_END.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (items.allReforged()) complete();
    }

    @Override
    public void onRenderScreen(GuiGraphicsExtractor graphics, Screen screen, String title, ScreenSlots slots) {
        if (!title.contains(REFORGE_SCREEN_TITLE)) return;

        if (isActive()) renderMissingReforgeList(graphics);
        
        ItemStack reforgeItem = slots.ALL.get(REFORGE_ITEM_SLOT_ID).getItem();
        String itemReforge = ItemUtils.getReforge(reforgeItem);

        if (reforgeItem.isEmpty()) highlightUnfinishedItems(graphics, slots.INVENTORY);
        else if (itemReforge != null) renderReforgeDisplay(screen, graphics, reforgeItem, itemReforge);
    }

    private static final Display reforgeDisplay = new Display("");

    public void renderReforgeDisplay(Screen screen, GuiGraphicsExtractor graphics, ItemStack reforgeItem, String reforge) {
        String id = ItemUtils.getId(reforgeItem);
        if (id.isEmpty()) return;
        
        // Check if we have a double chest open
        if (!(screen instanceof AbstractContainerScreen<?>)) return;
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        
        // Get double chest position and dimensions
        int chestHeight = accessor.getBackgroundHeight();
        
        // Check if reforge is valid for the specific item
        boolean isValidReforge = false;
        if (items.contains(id)) {
            ReforgeInfo info = items.get(id);
            isValidReforge = info.isValidReforge(reforge);
            if (isValidReforge) info.done = true;
            else info.done = false;
        }
        
        // Build the text to display with color based on validity
        String displayText = isValidReforge ? "&c" + reforge : "&e" + reforge;
        
        // Calculate chest container height (excluding player inventory)
        // Player inventory is typically 3 rows (27 slots) + 1 row for hotbar (9 slots) = 36 slots total
        // Each row is roughly 18 pixels high, so player inventory takes about 72 pixels
        int playerInventoryHeight = 72;
        int chestContainerHeight = chestHeight - playerInventoryHeight;
        
        // Calculate text dimensions
        int textWidth = RenderLib.getWidth(displayText);
        int textHeight = CLIENT.font.lineHeight; // Use actual font height
        
        // Position text inside the chest menu
        // Horizontal: center around 1/6 from left edge of chest container
        int chestContainerWidth = accessor.getBackgroundWidth();
        int centerX = chestContainerWidth / 4; // 1/4 away from left edge
        int textX = centerX - (textWidth / 2); // Center text around this point
        
        // Vertical: center on middle row of chest container (not including player inventory)
        int textY = (chestContainerHeight / 2) - (textHeight / 2) - 4; // - 4 to center on slot
        
        // Draw dark semi-transparent background
        int backgroundColor = 0xBF000000; // Dark semi-transparent (75% opacity)
        graphics.fill(textX - 5, textY - 3, textX + textWidth + 5, textY + textHeight + 2, backgroundColor);

        // Draw the text
        reforgeDisplay.setString(displayText).draw(graphics, textX, textY);
    }

    private static final Display itemsMissingReforge = new Display("");

    public void renderMissingReforgeList(GuiGraphicsExtractor graphics) {
        StringBuilder statusText = new StringBuilder("&cRemaining:\n");
        for (var entry : items.entrySet()) {
            String itemName = entry.getKey();
            ReforgeInfo info = entry.getValue();
            if (!info.done) {
                statusText.append("&f\n").append(itemName);
            }
        }

        statusText.append("\n\n&7You may need to unequip\n&7armor or take items out\n&7of ender chest.");

        int textWidth = RenderLib.getWidth(statusText.toString());
        
        int x = -textWidth - 10; // 10 pixels gap between text and chest
        int y = 20; // Align with top of chest with small offset
        
        itemsMissingReforge.setString(statusText.toString()).draw(graphics, x, y);
    }

    public void highlightUnfinishedItems(GuiGraphicsExtractor graphics, NonNullList<Slot> slots) {
        for (Slot slot : slots) {
            ItemStack item = slot.getItem();
            if (item.isEmpty()) continue;
            String id = ItemUtils.getId(item);
            if (id.isEmpty()) continue;
            if (!items.contains(id)) continue;
            if (items.get(id).done) continue;

            RenderLib.highlightSlot(graphics, slot, RenderLib.MINECRAFT_GOLD);
        }
    }

    @Override
    public void onClickSlot(Slot slot, int slotId, int button, ContainerInput actionType, CallbackInfo ci) {
        if (slotId != REFORGE_BUTTON_SLOT_ID) return;
        ItemStack reforgeItem = slot.container.getItem(REFORGE_ITEM_SLOT_ID);
        if (reforgeItem.isEmpty()) return;
        String itemId = ItemUtils.getId(reforgeItem);
        if (itemId.isEmpty() || !items.contains(itemId)) return;
        ReforgeInfo info = items.get(itemId);
        String reforge = ItemUtils.getReforge(reforgeItem);
        if(!info.isValidReforge(reforge)) return;
            
        ci.cancel();
    }

    @Override
    public void onScreenClose(Screen screen) {
        if (!(screen instanceof ContainerScreen)) return;
        if (isActive()) return;
        
        Events.RENDER_SCREEN.unregister(this);
        Events.CLICK_SLOT.unregister(this);
        Events.CLOSE_SCREEN.unregister(this);
    }
}