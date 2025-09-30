package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Skyblock;

import net.minecraft.client.MinecraftClient;

import java.util.Map;

public class HasStep extends Step implements
        ClientTickEventBus.ClientTickListener {
        
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
            boolean done = entry.getValue().done;

            if (done) formatted = formatted.replace("%" + (++i) + "%", "&a(✔)");
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
        ClientTickEventBus.register(this);
    }

    @Override
    protected void onDeactivate() {
        ClientTickEventBus.unregister(this);
    }

    @Override
    public void onClientTick(int tick) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world == null || mc.player == null || mc.player.age < 20) return;

        boolean allDone = true;

        // Update counts and done status for each item
        for (Map.Entry<String, ItemInfo> entry : items.entrySet()) {
            String itemId = entry.getKey();
            ItemInfo info = entry.getValue();

            int count = Skyblock.getItemCount(itemId);
            info.count = count;
            if (!info.done && count >= info.target) {
                info.done = true;
            }
            if (!info.done) {
                allDone = false;
            }
        }

        if (allDone) Guide.advance();
    }

}