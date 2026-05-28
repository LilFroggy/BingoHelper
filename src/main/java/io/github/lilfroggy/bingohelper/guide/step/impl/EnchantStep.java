package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.Display;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EnchantStep extends Step implements RenderScreenEvent, ClientTickEndEvent {

    public Map<String, ItemInfo> items;

    public static class ItemInfo {
        public List<String> enchants;
        public boolean done;
    }

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
        items.values().forEach(itemInfo -> {
            itemInfo.done = false;
        });
    }

    @Override
    protected void onActivate() {
        Events.CLIENT_TICK_END.register(this);
        Events.RENDER_SCREEN.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CLIENT_TICK_END.unregister(this);
        Events.RENDER_SCREEN.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (!(CLIENT.player instanceof ClientPlayerEntity player)) return;

        // Track if all items are done
        boolean allDone = true;

        // Check player inventory
        for (ItemStack stack : player.getInventory()) {
            if (stack == null || stack.isEmpty()) continue;

            String itemId = Skyblock.getID(stack);
            if (itemId.isEmpty()) continue;
            if (!items.containsKey(itemId)) continue;

            ItemInfo info = items.get(itemId);

            String lore = Skyblock.getLore(stack);
            boolean hasAllEnchants = info.enchants.stream().allMatch(enchant -> 
                lore.matches(".*\\b" + enchant.replace(" ", "\\s+") + "\\b.*"));
            if (hasAllEnchants) info.done = true;
            else info.done = false;
        }

        // Check container items (double chest)
        if (!(player.currentScreenHandler instanceof PlayerScreenHandler handler)) return;
        
        DefaultedList<ItemStack> containerItems = handler.getStacks();
        // Only check container slots (exclude player inventory slots which are the last 36 slots)
        int containerSlots = containerItems.size() - 36;
        for (int i = 0; i < containerSlots; i++) {
            ItemStack stack = containerItems.get(i);
            if (stack == null || stack.isEmpty()) continue;

            String itemId = Skyblock.getID(stack);
            if (itemId.isEmpty()) continue;
            if (!items.containsKey(itemId)) continue;

            ItemInfo info = items.get(itemId);

            String lore = Skyblock.getLore(stack);
            boolean hasAllEnchants = info.enchants.stream().allMatch(enchant -> 
                lore.matches(".*\\b" + enchant.replace(" ", "\\s+") + "\\b.*"));
            if (hasAllEnchants) info.done = true;
            else info.done = false;
        }

        // After checking all, see if all are done
        for (ItemInfo info : items.values()) {
            if (info.done) continue;
            allDone = false;
            break;
        }
        if (allDone) complete();
    }

    @Override
    public void onRenderScreen(DrawContext context, Screen screen, String title, DefaultedList<Slot> slots) {
        if (!title.contains("Enchant Item")) return;

        renderMissingEnchantList(screen, context);
        
        ItemStack enchantItem = slots.get(19).getStack();

        if (enchantItem.isEmpty()) highlightUnfinishedItems(context, slots);
        else highlightMissingEnchants(context, enchantItem, slots);
    }

    private static final Display itemsMissingEnchants = new Display("");

    public void renderMissingEnchantList(Screen screen, DrawContext drawContext) {
        StringBuilder statusText = new StringBuilder("&cRemaining:\n");
        for (Map.Entry<String, ItemInfo> entry : items.entrySet()) {
            String itemName = entry.getKey();
            ItemInfo info = entry.getValue();
            if (!info.done) {
                statusText.append("&f\n").append(itemName);
            }
        }

        statusText.append("\n\n&7You may need to level\n&7enchanting or visit the\n&7next page to view some\n&7enchants.");

        int textWidth = RenderLib.getFormattedStringWidth(statusText.toString());
        
        int x = -textWidth - 10; // 10 pixels gap between text and chest
        int y = 20; // Align with top of chest with small offset
        
        itemsMissingEnchants.setString(statusText.toString()).draw(drawContext, x, y);
    }

    public void highlightUnfinishedItems(DrawContext drawContext, DefaultedList<Slot> slots) {
        for (Slot slot : slots) {
            if (!(slot.inventory instanceof PlayerInventory)) continue;
            ItemStack item = slot.getStack();
            if (item.isEmpty()) continue;
            String itemId = Skyblock.getID(item);
            if (itemId.isEmpty()) continue;
            if (!items.containsKey(itemId)) continue;
            if (items.get(itemId).done) continue;

            RenderLib.highlightSlot(drawContext, slot, RenderLib.MINECRAFT_GOLD);
        }
    }

    public void highlightMissingEnchants(DrawContext drawContext, ItemStack enchantItem, DefaultedList<Slot> slots) {
        String enchantItemId = Skyblock.getID(enchantItem);
        if (enchantItemId.isEmpty()) return;
        if (!items.containsKey(enchantItemId)) return;
        List<String> requiredEnchants = items.get(enchantItemId).enchants;

        for (Slot slot : slots) {
            if (slot.inventory instanceof PlayerInventory) continue;
            ItemStack item = slot.getStack();
            if (item.isEmpty()) continue;
            if (!(item.getCustomName() instanceof Text customName)) continue;
            String itemName = customName.getString();
            if (itemName == null || itemName.trim().isEmpty()) continue;
            String enchantItemLore = Skyblock.getLore(enchantItem);
            boolean required = requiredEnchants.stream().anyMatch(e -> !enchantItemLore.contains(e) && e.matches(Pattern.quote(itemName) + "\\b.*"));
            if (!required) continue;

            RenderLib.highlightSlot(drawContext, slot, RenderLib.MINECRAFT_GREEN);
        }
    }

}