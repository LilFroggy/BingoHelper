package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.ScreenRenderEventBus;
import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EnchantStep extends Step implements
        ScreenRenderEventBus.ScreenRenderListener,
        ClientTickEventBus.ClientTickListener {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public Map<String, ItemInfo> items;

    public static class ItemInfo {
        public List<String> enchants;
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
    }

    @Override
    protected void onDeactivate() {
        ClientTickEventBus.unregister(this);
        ScreenRenderEventBus.unregister(this);
    }

    @Override
    public void onClientTick(int tick) {
        if (CLIENT.player == null) return;

        // Track if all items are done
        boolean allDone = true;

        // Check player inventory
        for (ItemStack stack : CLIENT.player.getInventory()) {
            if (stack == null || stack.isEmpty()) continue;

            String itemId = Skyblock.getID(stack);
            if (itemId == null) continue;
            if (!items.containsKey(itemId)) continue;

            ItemInfo info = items.get(itemId);

            String lore = Skyblock.getLore(stack);
            boolean hasAllEnchants = info.enchants.stream().allMatch(enchant -> 
                lore.matches(".*\\b" + enchant.replace(" ", "\\s+") + "\\b.*"));
            if (hasAllEnchants) info.done = true;
            else info.done = false;
        }

        // Check container items (double chest)
        if (CLIENT.player.currentScreenHandler != null && !(CLIENT.player.currentScreenHandler instanceof PlayerScreenHandler)) {
            DefaultedList<ItemStack> containerItems = CLIENT.player.currentScreenHandler.getStacks();
            // Only check container slots (exclude player inventory slots which are the last 36 slots)
            int containerSlots = containerItems.size() - 36;
            for (int i = 0; i < containerSlots; i++) {
                ItemStack stack = containerItems.get(i);
                if (stack == null || stack.isEmpty()) continue;

                String itemId = Skyblock.getID(stack);
                if (itemId == null) continue;
                if (!items.containsKey(itemId)) continue;

                ItemInfo info = items.get(itemId);

                String lore = Skyblock.getLore(stack);
                boolean hasAllEnchants = info.enchants.stream().allMatch(enchant -> 
                    lore.matches(".*\\b" + enchant.replace(" ", "\\s+") + "\\b.*"));
                if (hasAllEnchants) info.done = true;
                else info.done = false;
            }
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
        if (!title.contains("Enchant Item")) return;

        renderMissingEnchantList(screen, context);
        
        ItemStack enchantItem = slots.get(19).getStack();

        if (enchantItem.isEmpty()) highlightUnfinishedItems(context, slots);
        else highlightMissingEnchants(context, enchantItem, slots);
    }

    public void renderMissingEnchantList(Screen screen, DrawContext drawContext) {
        StringBuilder statusText = new StringBuilder("&cMissing:\n\n");
        for (Map.Entry<String, ItemInfo> entry : items.entrySet()) {
            String itemName = entry.getKey();
            ItemInfo info = entry.getValue();
            if (!info.done) {
                statusText.append("&f").append(itemName).append("\n");
            }
        }

        // Add footer
        statusText.append("\n&7You may need to level\n&7enchanting or visit the\n&7next page to view some\n&7enchants.");
        
        // Calculate text width and height to ensure proper positioning
        int textWidth = RenderLib.getFormattedStringWidth(statusText.toString());
        
        // Position text to the left of the double chest with 10-pixel gap
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
            String itemId = Skyblock.getID(item);
            if (itemId == null) continue;
            if (!items.containsKey(itemId)) continue;
            if (items.get(itemId).done) continue;
            RenderLib.highlightSlot(drawContext, slot, RenderLib.MINECRAFT_GOLD);
        }
    }

    public void highlightMissingEnchants(DrawContext drawContext, ItemStack enchantItem, DefaultedList<Slot> slots) {
        String enchantItemId = Skyblock.getID(enchantItem);
        if (!items.containsKey(enchantItemId)) return;
        List<String> requiredEnchants = items.get(enchantItemId).enchants;
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot.inventory instanceof PlayerInventory) continue;
            ItemStack item = slot.getStack();
            if (item.isEmpty()) continue;
            if (item.getCustomName() == null) continue;
            String itemName = item.getCustomName().getString();
            if (itemName == null || itemName.trim().isEmpty()) continue;
            String enchantItemLore = Skyblock.getLore(enchantItem);
            boolean required = requiredEnchants.stream().anyMatch(e -> !enchantItemLore.contains(e) && e.matches(Pattern.quote(itemName) + "\\b.*"));
            if (!required) continue;
            RenderLib.highlightSlot(drawContext, slot, RenderLib.MINECRAFT_GREEN);
        }
    }

}