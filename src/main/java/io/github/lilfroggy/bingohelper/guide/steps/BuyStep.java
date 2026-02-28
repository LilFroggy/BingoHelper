package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.ChatEventBus;
import io.github.lilfroggy.bingohelper.events.SlotRenderEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class BuyStep extends Step implements
        ChatEventBus.GameMessageListener,
        SlotRenderEventBus.SlotRenderListener {

    public Map<String, ItemInfo> items;

    public static class ItemInfo {
        public int count;
        public int target;
        public boolean done;
    }

    @Override
    public String additionalInstructionFormatting() {
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
    public void onReset() {
        items.values().forEach(itemInfo -> {
            itemInfo.count = 0;
            itemInfo.done = false;
        });
    }

    @Override
    protected void onActivate() {
        ChatEventBus.register(this);
        SlotRenderEventBus.register(this);
    }

    @Override
    protected void onDeactivate() {
        ChatEventBus.unregister(this);
        SlotRenderEventBus.unregister(this);
    }

    private static final Pattern BUY_SINGLE_REGEX = Pattern.compile("^You bought (.+)!$");
    private static final Pattern BUY_MULTIPLE_REGEX = Pattern.compile("^You bought (.+) x(\\d+)(?: for (?:\\d+) Coins)?!$");

    private String boughtName = null;
    private Integer boughtCount = null;

    @Override
    public void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) return;

        Matcher multiMatcher = BUY_MULTIPLE_REGEX.matcher(unformattedMsg);
        Matcher singleMatcher = BUY_SINGLE_REGEX.matcher(unformattedMsg);

        if (multiMatcher.find()) {
            String name = multiMatcher.group(1);
            String countStr = multiMatcher.group(2);
            int count = Integer.parseInt(countStr);
            boughtName = name;
            boughtCount = count;
            //Logger.info("Bought: " + boughtName + " - " + boughtCount);
            return;
        }
        if (singleMatcher.find()) {
            String name = singleMatcher.group(1);
            boughtName = name;
            boughtCount = 1;
            //Logger.info("Bought: " + boughtName + " - " + boughtCount);
            return;
        }
    }

    @Override
    public void onSlotRender(DrawContext context, Slot slot) {
        if (slot.inventory instanceof PlayerInventory) return;
        ItemStack item = slot.getStack();
        if (item.isEmpty()) return;
        String id = Skyblock.getID(item);
        if (id == null) return;
        if (!items.containsKey(id)) return;
        ItemInfo itemInfo = items.get(id);

        if (!itemInfo.done) RenderLib.highlightSlot(context, slot, RenderLib.MINECRAFT_GREEN);

        if (boughtName == null || boughtCount == null) return;

        String itemName = item.getName().getString();
        if (itemName == null) return;
        itemName = itemName.replaceAll("x\\d+", "").trim();
        if (!itemName.equals(boughtName)) return;

        itemInfo.count += boughtCount;
        if (itemInfo.count >= itemInfo.target) itemInfo.done = true;

        boughtName = null;
        boughtCount = null;
        if (items.values().stream().allMatch(info -> info.done)) Guide.advance();
    }
}