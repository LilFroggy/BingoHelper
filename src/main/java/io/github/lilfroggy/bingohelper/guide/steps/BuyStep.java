package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.ChatEventBus;
import io.github.lilfroggy.bingohelper.events.ScreenRenderEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class BuyStep extends Step implements
        ChatEventBus.GameMessageListener,
        ScreenRenderEventBus.ScreenRenderListener {

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
        ScreenRenderEventBus.register(this);
    }

    @Override
    protected void onDeactivate() {
        ChatEventBus.unregister(this);
        ScreenRenderEventBus.unregister(this);
    }

    private static final Pattern BUY_SINGLE_REGEX = Pattern.compile("^You bought (.+?) for (.+?) Coins!$");
    private static final Pattern BUY_MULTIPLE_REGEX = Pattern.compile("^You bought (.+?) x(.+?) for (.+?) Coins!$");

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
            //ChatLib.chat("Bought: " + boughtName + " - " + boughtCount);
            return;
        }
        if (singleMatcher.find()) {
            String name = singleMatcher.group(1);
            boughtName = name;
            boughtCount = 1;
            //ChatLib.chat("Bought: " + boughtName + " - " + boughtCount);
            return;
        }
    }

    @Override
    public void onScreenRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;

        if (player == null || player.currentScreenHandler == null) return;
        ScreenHandler screenHandler = player.currentScreenHandler;

        // Check if it's a container (not the player's inventory)
        if (screenHandler instanceof PlayerScreenHandler) return;

        DefaultedList<ItemStack> containerItems = screenHandler.getStacks();

        for (int i = 0; i < containerItems.size() - 36; i++) {
            ItemStack item = containerItems.get(i);
            if (item.isEmpty()) continue;

            String itemId = Skyblock.getID(item);

            if (itemId == null) continue;

            if (!items.containsKey(itemId)) continue;

            ItemInfo itemInfo = items.get(itemId);
            if (!itemInfo.done) RenderLib.highlightContainerSlot(drawContext, i, RenderLib.MINECRAFT_GREEN);

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

        if (items.values().stream().allMatch(info -> info.done)) Guide.advance();
    }
}