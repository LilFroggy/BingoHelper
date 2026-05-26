package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Skyblock;

import java.util.Map;

public class HasStep extends Step implements ClientTickEndEvent {
        
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

        for (var entry : items.entrySet()) {
            int count = entry.getValue().count;
            int target = entry.getValue().target;
            boolean done = entry.getValue().done;

            if (done) formatted = formatted.replace("%" + (++i) + "%", "&a(✔)");
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
        Events.CLIENT_TICK_END.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CLIENT_TICK_END.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (CLIENT.world == null || CLIENT.player == null || CLIENT.player.age < 20) return;

        boolean allDone = true;

        for (var entry : items.entrySet()) {
            String id = entry.getKey();
            ItemInfo info = entry.getValue();

            int count = Skyblock.getItemCount(id);
            info.count = count;

            if (!info.done && count >= info.target) {
                info.done = true;
            }
            
            if (!info.done) {
                allDone = false;
            }
        }

        if (allDone) complete();
    }
}