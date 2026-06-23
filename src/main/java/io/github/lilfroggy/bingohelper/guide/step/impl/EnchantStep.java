package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.ScreenUtils;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.Display;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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
        var containerSlots = ScreenUtils.getSlots();

        if (containerSlots.isEmpty()) checkPlayerSlots();
        else checkContainerSlots(containerSlots);

        boolean allDone = items.values().stream().allMatch(info -> info.done);
        if (allDone) complete();
    }

    private void checkPlayerSlots() {
        if (CLIENT.player == null) return;

        for (ItemStack item : CLIENT.player.getInventory()) {
            checkItem(item);
        }
    }

    private void checkContainerSlots(NonNullList<Slot> slots) {
        boolean inEnchantTable = ScreenUtils.getTitle().contains("Enchant Item");

        for (Slot slot : slots) {
            boolean isPlayerSlot = slot.container instanceof Inventory;
            if (!inEnchantTable && !isPlayerSlot) continue;
            checkItem(slot.getItem());
        }
    }

    private void checkItem(ItemStack item) {
        if (item == null || item.isEmpty()) return;
        String id = Skyblock.getID(item);
        if (id.isEmpty()) return;
        if (!items.containsKey(id)) return;

        ItemInfo info = items.get(id);

        String lore = Skyblock.getLore(item);
        boolean hasAllEnchants = info.enchants.stream().allMatch(enchant -> 
            lore.matches(".*\\b" + enchant.replace(" ", "\\s+") + "\\b.*"));
        if (hasAllEnchants) info.done = true;
        else info.done = false;
    }

    @Override
    public void onRenderScreen(GuiGraphics graphics, Screen screen, String title, NonNullList<Slot> slots) {
        if (!title.contains("Enchant Item")) return;

        renderMissingEnchantList(screen, graphics);
        
        ItemStack enchantItem = slots.get(19).getItem();

        if (enchantItem.isEmpty()) highlightUnfinishedItems(graphics, slots);
        else highlightMissingEnchants(graphics, enchantItem, slots);
    }

    private static final Display itemsMissingEnchants = new Display("");

    public void renderMissingEnchantList(Screen screen, GuiGraphics graphics) {
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
        
        itemsMissingEnchants.setString(statusText.toString()).draw(graphics, x, y);
    }

    public void highlightUnfinishedItems(GuiGraphics graphics, NonNullList<Slot> slots) {
        for (Slot slot : slots) {
            if (!(slot.container instanceof Inventory)) continue;
            ItemStack item = slot.getItem();
            if (item.isEmpty()) continue;
            String itemId = Skyblock.getID(item);
            if (itemId.isEmpty()) continue;
            if (!items.containsKey(itemId)) continue;
            if (items.get(itemId).done) continue;

            RenderLib.highlightSlot(graphics, slot, RenderLib.MINECRAFT_GOLD);
        }
    }

    public void highlightMissingEnchants(GuiGraphics graphics, ItemStack enchantItem, NonNullList<Slot> slots) {
        String enchantItemId = Skyblock.getID(enchantItem);
        if (enchantItemId.isEmpty()) return;
        if (!items.containsKey(enchantItemId)) return;
        List<String> requiredEnchants = items.get(enchantItemId).enchants;

        for (Slot slot : slots) {
            if (slot.container instanceof Inventory) continue;
            ItemStack item = slot.getItem();
            if (item.isEmpty()) continue;
            if (!(item.getCustomName() instanceof Component customName)) continue;
            String itemName = customName.getString();
            if (itemName == null || itemName.trim().isEmpty()) continue;
            String enchantItemLore = Skyblock.getLore(enchantItem);
            boolean required = requiredEnchants.stream().anyMatch(e -> !enchantItemLore.contains(e) && e.matches(Pattern.quote(itemName) + "\\b.*"));
            if (!required) continue;

            RenderLib.highlightSlot(graphics, slot, RenderLib.MINECRAFT_GREEN);
        }
    }

}