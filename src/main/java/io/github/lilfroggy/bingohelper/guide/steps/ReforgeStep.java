package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.ScreenRenderEventBus;
import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.events.GuiCloseEventBus;
import io.github.lilfroggy.bingohelper.events.MouseClickEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.mixin.HandledScreenAccessorMixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

public class ReforgeStep extends Step implements
        ScreenRenderEventBus.ScreenRenderListener,
        ClientTickEventBus.ClientTickListener,
        MouseClickEventBus.MouseClickListener,
        GuiCloseEventBus.GuiCloseListener {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public Map<String, ItemInfo> items;

    public static class ItemInfo {
        public List<String> reforges;
        public boolean done;
    }

    @Override
    public String additionalInstructionFormatting() {
        return instruction;
    }

    @Override
    public void onReset() {
        items.values().forEach(itemInfo -> {
            itemInfo.done = false;
        });
    }

    @Override
    protected void onActivate() {
        ClientTickEventBus.register(this);
        ScreenRenderEventBus.register(this);
        MouseClickEventBus.register(this);
        GuiCloseEventBus.register(this);
    }

    @Override
    protected void onDeactivate() {
        ClientTickEventBus.unregister(this);
    }

    @Override
    public void onClientTick(int tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        // Track if all items are done
        boolean allDone = true;

        // Check player inventory
        for (ItemStack stack : mc.player.getInventory()) {
            if (stack == null || stack.isEmpty()) continue;

            String itemId = Skyblock.getID(stack);
            if (itemId == null) continue;
            if (!items.containsKey(itemId)) continue;

            ItemInfo info = items.get(itemId);

            String itemReforge = Skyblock.getReforge(stack);
            if (itemReforge == null || itemReforge.isEmpty()) continue;

            itemReforge = ChatLib.toTitleCase(itemReforge);

            boolean hasReforge = info.reforges.contains(itemReforge);
            if (hasReforge) info.done = true;
            else info.done = false;
        }

        // After checking all, see if all are done
        for (ItemInfo info : items.values()) {
            if (info.done) continue;
            allDone = false;
            break;
        }
        if (allDone) Guide.advance();
    }

    @Override
    public void onScreenRender(DrawContext context, Screen screen, String title, DefaultedList<Slot> slots) {
        if (!title.contains("Reforge Item")) return;

        renderMissingReforgeList(context);
        
        ItemStack reforgeItem = slots.get(13).getStack();

        String itemReforge = Skyblock.getReforge(reforgeItem);

        if (itemReforge == null || reforgeItem.isEmpty()) highlightUnfinishedItems(context, slots);
        else renderReforgeDisplay(screen, context, reforgeItem, itemReforge);
    }

    public void renderReforgeDisplay(Screen screen, DrawContext drawContext, ItemStack reforgeItem, String reforge) {
        String id = Skyblock.getID(reforgeItem);
        if (id == null) return;

        if (CLIENT.textRenderer == null) return;
        
        // Check if we have a double chest open
        if (!(screen instanceof HandledScreen<?>)) return;
        HandledScreenAccessorMixin accessor = (HandledScreenAccessorMixin) screen;
        
        // Get double chest position and dimensions
        int chestHeight = accessor.getBackgroundHeight();
        
        // Check if reforge is valid for the specific item
        boolean isValidReforge = false;
        if (items.containsKey(id)) {
            ItemInfo info = items.get(id);
            isValidReforge = info.reforges.contains(reforge);
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
        int textWidth = RenderLib.getFormattedStringWidth(displayText);
        int textHeight = CLIENT.textRenderer.fontHeight; // Use actual font height
        
        // Position text inside the chest menu
        // Horizontal: center around 1/6 from left edge of chest container
        int chestContainerWidth = accessor.getBackgroundWidth();
        int centerX = chestContainerWidth / 4; // 1/4 away from left edge
        int textX = centerX - (textWidth / 2); // Center text around this point
        
        // Vertical: center on middle row of chest container (not including player inventory)
        int textY = (chestContainerHeight / 2) - (textHeight / 2) - 4; // - 4 to center on slot
        
        // Draw dark semi-transparent background
        int backgroundColor = 0xBF000000; // Dark semi-transparent (75% opacity)
        drawContext.fill(textX - 5, textY - 3, textX + textWidth + 5, textY + textHeight + 2, backgroundColor);

        // Draw the text
        RenderLib.drawFormattedString(drawContext, displayText, textX, textY);
    }

    public void renderMissingReforgeList(DrawContext drawContext) {
        StringBuilder statusText = new StringBuilder("&cMissing:\n\n");
        for (Map.Entry<String, ItemInfo> entry : items.entrySet()) {
            String itemName = entry.getKey();
            ItemInfo info = entry.getValue();
            if (!info.done) {
                statusText.append("&f").append(itemName).append("\n");
            }
        }

        int textWidth = RenderLib.getFormattedStringWidth(statusText.toString());
        
        int x = -textWidth - 10; // 10 pixels gap between text and chest
        int y = 10; // Align with top of chest with small offset
        
        RenderLib.drawFormattedString(drawContext, statusText.toString(), x, y);
    }

    public void highlightUnfinishedItems(DrawContext drawContext, DefaultedList<Slot> slots) {
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (!(slot.inventory instanceof PlayerInventory)) continue;
            ItemStack item = slot.getStack();
            if (item.isEmpty()) continue;
            String id = Skyblock.getID(item);
            if (id == null) continue;
            if (!items.containsKey(id)) continue;
            if (items.get(id).done) continue;
            RenderLib.highlightSlot(drawContext, slot, RenderLib.MINECRAFT_GOLD);
        }
    }

    @Override
    public void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        // Check if this is slot 22 (reforge slot)
        if (slotId != 22) return;
        // Get the reforge item (slot 13)
        ItemStack reforgeItem = slot.inventory.getStack(13);
        if (reforgeItem.isEmpty()) return;
        String itemId = Skyblock.getID(reforgeItem);
        if (itemId == null || !items.containsKey(itemId)) return;
        ItemInfo info = items.get(itemId);
        String reforge = Skyblock.getReforge(reforgeItem);
        if (reforge == null) return;
        boolean isValidReforge = info.reforges.contains(reforge);
        if(!isValidReforge) return;
            
        ci.cancel();
    }

    @Override
    public void onGuiClose(Screen screen) {
        if (!(screen instanceof GenericContainerScreen)) return;
        if (isActive) return;
        ScreenRenderEventBus.unregister(this);
        MouseClickEventBus.unregister(this);
        GuiCloseEventBus.unregister(this);
    }
}