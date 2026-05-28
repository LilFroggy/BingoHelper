package io.github.lilfroggy.bingohelper.util.slot;

import java.util.HashMap;
import java.util.Map;

import io.github.lilfroggy.bingohelper.events.Events;

public class SlotRegistry {
    private static final Map<SlotPredicate, SlotPredicate> REGISTRY = new HashMap<>();

    static {
        Events.CLIENT_TICK_END.register(SlotRegistry::onClientTickEnd);
    }

    public static SlotPredicate getOrCreate(SlotPredicate prospective) {
        return REGISTRY.computeIfAbsent(prospective, key -> {
            key.incrementRef();
            return key;
        });
    }

    public static void release(SlotPredicate canonical) {
        canonical.decrementRef();
        if (canonical.getRefCount() <= 0) {
            REGISTRY.remove(canonical);
        }
    }

    public static void onClientTickEnd(int tick) {
        if (REGISTRY.isEmpty()) return;
        for (SlotPredicate p : REGISTRY.values()) {
            p.scanInventory();
        }
    }
}