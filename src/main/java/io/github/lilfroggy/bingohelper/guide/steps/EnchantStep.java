package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.ScreenRenderEventBus;
import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.mixin.HandledScreenAccessorMixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EnchantStep extends Step implements
        ScreenRenderEventBus.ScreenRenderListener,
        ClientTickEventBus.ClientTickListener {

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

            String lore = Skyblock.getLore(stack);
            boolean hasAllEnchants = info.enchants.stream().allMatch(enchant -> 
                lore.matches(".*\\b" + enchant.replace(" ", "\\s+") + "\\b.*"));
            if (hasAllEnchants) info.done = true;
            else info.done = false;
        }

        // Check container items (double chest)
        if (mc.player.currentScreenHandler != null && !(mc.player.currentScreenHandler instanceof PlayerScreenHandler)) {
            DefaultedList<ItemStack> containerItems = mc.player.currentScreenHandler.getStacks();
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
    public void onScreenRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        if (screen.getTitle() == null || screen.getTitle().getString().isEmpty()) return;
        if (!screen.getTitle().getString().contains("Enchant Item")) return;

        renderEnchantOverlay(screen, drawContext);

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.player.currentScreenHandler == null) return;
        ScreenHandler screenHandler = mc.player.currentScreenHandler;

        // Check if it's a container (not the player's inventory)
        if (screenHandler instanceof PlayerScreenHandler) return;

        DefaultedList<ItemStack> containerItems = screenHandler.getStacks();
        ItemStack enchantItem = containerItems.get(19);
        if (enchantItem.isEmpty()) highlightUnfinishedItems(drawContext);
        else highlightMissingEnchants(drawContext, enchantItem, containerItems, drawContext);
    }

    public void renderEnchantOverlay(Screen screen, DrawContext drawContext) {
        // Check if we have a double chest open
        if (!(screen instanceof HandledScreen<?>)) return;
        HandledScreenAccessorMixin accessor = (HandledScreenAccessorMixin) screen;
        
        // Get double chest position and dimensions
        int chestX = accessor.getScreenX();
        int chestY = accessor.getScreenY();
        
        // Build status text first to calculate its width
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
        int textX = chestX - textWidth - 10; // 10 pixels gap between text and chest
        int textY = chestY + 10; // Align with top of chest with small offset
        
        RenderLib.drawFormattedString(drawContext, statusText.toString(), textX, textY);
    }

    public void highlightUnfinishedItems(DrawContext drawContext) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        for (int i = 0; i < mc.player.getInventory().getMainStacks().size(); i++) {
            ItemStack item = mc.player.getInventory().getMainStacks().get(i);
            if (item.isEmpty()) continue;
            String itemId = Skyblock.getID(item);
            if (itemId == null) continue;
            if (!items.containsKey(itemId)) continue;
            if (items.get(itemId).done) continue;
            RenderLib.highlightPlayerSlot(drawContext, i, RenderLib.MINECRAFT_GOLD);
        }
    }

    public void highlightMissingEnchants(DrawContext drawContext, ItemStack enchantItem, DefaultedList<ItemStack> containerItems, DrawContext ctx) {
        String enchantItemId = Skyblock.getID(enchantItem);
        if (!items.containsKey(enchantItemId)) return;
        List<String> requiredEnchants = items.get(enchantItemId).enchants;
        for (int i = 0; i < containerItems.size() - 36; i++) {
            ItemStack item = containerItems.get(i);
            if (item.isEmpty()) continue;
            if (item.getCustomName() == null) continue;
            String itemName = item.getCustomName().getString();
            if (itemName == null || itemName.trim().isEmpty()) continue;
            String enchantItemLore = Skyblock.getLore(enchantItem);
            boolean required = requiredEnchants.stream().anyMatch(e -> !enchantItemLore.contains(e) && e.matches(Pattern.quote(itemName) + "\\b.*"));
            if (!required) continue;
            RenderLib.highlightContainerSlot(drawContext, i, RenderLib.MINECRAFT_GREEN);
        }
    }

}