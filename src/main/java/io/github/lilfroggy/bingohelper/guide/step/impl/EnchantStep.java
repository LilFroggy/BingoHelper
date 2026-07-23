package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.ItemUtils;
import io.github.lilfroggy.bingohelper.util.ScreenUtils.ScreenSlots;
import io.github.lilfroggy.bingohelper.util.item.EnchantInfo;
import io.github.lilfroggy.bingohelper.util.item.EnchantList;
import io.github.lilfroggy.bingohelper.util.render.Display;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EnchantStep extends Step implements RenderScreenEvent, ClientTickEndEvent {

    EnchantList items;

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
    }

    @Override
    protected void onDeactivate() {
        Events.CLIENT_TICK_END.unregister(this);
        Events.RENDER_SCREEN.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (items.allEnchanted()) complete();
    }

    @Override
    public void onRenderScreen(GuiGraphicsExtractor graphics, Screen screen, String title, ScreenSlots slots) {
        if (!title.contains("Enchant Item")) return;

        renderMissingEnchantList(screen, graphics);
        
        ItemStack enchantItem = slots.ALL.get(19).getItem();

        if (enchantItem.isEmpty()) highlightUnfinishedItems(graphics, slots.INVENTORY);
        else highlightMissingEnchants(graphics, enchantItem, slots.CONTAINER);
    }

    private static final Display itemsMissingEnchants = new Display("");

    public void renderMissingEnchantList(Screen screen, GuiGraphicsExtractor graphics) {
        StringBuilder statusText = new StringBuilder("&cRemaining:\n");
        for (Map.Entry<String, EnchantInfo> entry : items.entrySet()) {
            String itemName = entry.getKey();
            EnchantInfo info = entry.getValue();
            if (!info.done) {
                statusText.append("&f\n").append(itemName);
            }
        }

        statusText.append("\n\n&7You may need to level\n&7enchanting or visit the\n&7next page to view some\n&7enchants.");

        int textWidth = RenderLib.getWidth(statusText.toString());
        
        int x = -textWidth - 10; // 10 pixels gap between text and chest
        int y = 20; // Align with top of chest with small offset
        
        itemsMissingEnchants.setString(statusText.toString()).draw(graphics, x, y);
    }

    public void highlightUnfinishedItems(GuiGraphicsExtractor graphics, NonNullList<Slot> slots) {
        for (Slot slot : slots) {
            ItemStack item = slot.getItem();
            if (item.isEmpty()) continue;
            String itemId = ItemUtils.getId(item);
            if (itemId.isEmpty()) continue;
            if (!items.contains(itemId)) continue;
            if (items.get(itemId).done) continue;

            RenderLib.highlightSlot(graphics, slot, RenderLib.MINECRAFT_GOLD);
        }
    }

    public void highlightMissingEnchants(GuiGraphicsExtractor graphics, ItemStack enchantItem, NonNullList<Slot> slots) {
        String enchantItemId = ItemUtils.getId(enchantItem);
        if (enchantItemId.isEmpty()) return;
        if (!items.contains(enchantItemId)) return;
        List<String> requiredEnchants = items.get(enchantItemId).requiredEnchants();

        for (Slot slot : slots) {
            ItemStack item = slot.getItem();
            if (item.isEmpty()) continue;
            String itemName = item.getHoverName().getString();
            if (itemName.isEmpty()) continue;
            String enchantItemLore = ItemUtils.getLore(enchantItem);
            boolean required = requiredEnchants.stream().anyMatch(e -> !enchantItemLore.contains(e) && e.matches(Pattern.quote(itemName) + "\\b.*"));
            if (!required) continue;

            RenderLib.highlightSlot(graphics, slot, RenderLib.MINECRAFT_GREEN);
        }
    }

}