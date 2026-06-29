package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.MessageEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.ScreenUtils;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.item.HasInfo;
import io.github.lilfroggy.bingohelper.util.item.HasList;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class BuyStep extends Step implements MessageEvent, RenderScreenEvent {

    public HasList items;

    @Override
    public String formattedInstruction() {
        String formatted = instruction;
        int i = 1;

        for (var entry : items.entrySet()) {
            String placeholder = "%" + (i++) + "%";
            var info = entry.getValue();

            if (info.done()) formatted = formatted.replace(placeholder, "&a(✔)");
            else formatted = formatted.replace(placeholder, "(" + info.count() + "/" + info.target() + ")");
        }
        return formatted;
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
    public void onRenderScreen(GuiGraphicsExtractor context, Screen screen, String title, NonNullList<Slot> slots) {
        int lowest = Integer.MAX_VALUE;
        int highest = 0;
        Slot best = null;
        Slot bestFallback = null;

        for (Slot slot : slots) {
            if (slot.container instanceof Inventory) continue;
            ItemStack item = slot.getItem();
            if (item.isEmpty()) continue;
            String id = Skyblock.getID(item);
            if (id.isEmpty()) continue;
            if (!items.contains(id)) continue;
            HasInfo itemInfo = items.get(id);

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

            String itemName = item.getHoverName().getString();
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
        if (items.allDone()) complete();
    }
}