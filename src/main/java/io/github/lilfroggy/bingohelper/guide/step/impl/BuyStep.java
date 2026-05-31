package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.MessageEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.ScreenUtils;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class BuyStep extends Step implements MessageEvent, RenderScreenEvent {

    public Map<String, ItemInfo> items;

    public static class ItemInfo {
        public int count;
        public int target;
        public boolean done;
    }

    @Override
    public String formattedInstruction() {
        String formatted = instruction;

        int i = 0;

        for (Map.Entry<String, ItemInfo> entry : items.entrySet()) {
            int count = entry.getValue().count;
            int target = entry.getValue().target;

            if (count >= target) formatted = formatted.replace("%" + (++i) + "%", "&a(✔)");
            else formatted = formatted.replace("%" + (++i) + "%", "(" + count + "/" + target + ")");
        }

        return formatted;
    }

    @Override
    public void onInit() {
        // Nothing to reset
    }

    @Override
    public void onReset() {
        items.values().forEach(itemInfo -> {
            itemInfo.count = 0;
            itemInfo.done = false;
        });
    }

    @Override
    protected void onActivate() {
        Events.MESSAGE.register(this);
        Events.RENDER_SCREEN.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.MESSAGE.unregister(this);
        Events.RENDER_SCREEN.unregister(this);
    }

    private static final Pattern BUY_SINGLE_REGEX = Pattern.compile("^You bought (.+?)(?: for (?:.+) Coins)?!$");
    private static final Pattern BUY_MULTIPLE_REGEX = Pattern.compile("^You bought (.+) x(\\d+)(?: for (?:.+) Coins)?!$");

    private String boughtName = null;
    private Integer boughtCount = null;

    @Override
    public void onMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        Matcher multiMatcher = BUY_MULTIPLE_REGEX.matcher(unformattedMsg);
        Matcher singleMatcher = BUY_SINGLE_REGEX.matcher(unformattedMsg);

        if (multiMatcher.find()) {
            String name = multiMatcher.group(1);
            String countStr = multiMatcher.group(2);
            int count = Integer.parseInt(countStr);
            boughtName = name;
            boughtCount = count;
            if (Config.debug) Logger.info("Bought: " + boughtName + " - " + boughtCount);
            return;
        }
        if (singleMatcher.find()) {
            String name = singleMatcher.group(1);
            boughtName = name;
            boughtCount = 1;
            if (Config.debug) Logger.info("Bought: " + boughtName + " - " + boughtCount);
            return;
        }
    }

    @Override
    public void onRenderScreen(DrawContext context, Screen screen, String title, DefaultedList<Slot> slots) {
        int lowest = Integer.MAX_VALUE;
        int highest = 0;
        Slot best = null;
        Slot bestFallback = null;

        for (Slot slot : slots) {
            if (slot.inventory instanceof PlayerInventory) continue;
            ItemStack item = slot.getStack();
            if (item.isEmpty()) continue;
            String id = Skyblock.getID(item);
            if (id.isEmpty()) continue;
            if (!items.containsKey(id)) continue;
            ItemInfo itemInfo = items.get(id);

            if (!itemInfo.done) {
                if (title.equals("Shop Trading Options")) {
                    int amount = item.getCount();
                    boolean isBetter = amount > highest && amount <= itemInfo.target - itemInfo.count;
                    if (isBetter) {
                        highest = amount;
                        best = slot;
                    }
                    if (amount < lowest) {
                        lowest = amount;
                        bestFallback = slot;
                    }
                }
                else RenderLib.highlightSlot(context, slot, RenderLib.MINECRAFT_GREEN);
            }

            if (boughtName == null || boughtCount == null) continue;

            String itemName = item.getName().getString();
            if (itemName == null) continue;
            itemName = itemName.replaceAll("x\\d+", "").trim();
            if (!itemName.equals(boughtName)) continue;

            itemInfo.count += boughtCount;
            if (itemInfo.count >= itemInfo.target) itemInfo.done = true;

            boughtName = null;
            boughtCount = null;
        }

        if (!ScreenUtils.getCursorStack().isEmpty()) return;
        if (best == null) best = bestFallback;
        if (best != null) RenderLib.highlightSlot(context, best, RenderLib.MINECRAFT_GREEN);
        if (items.values().stream().allMatch(info -> info.done)) complete();
    }
}