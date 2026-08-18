package io.github.lilfroggy.bingohelper.util.slot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.lilfroggy.bingohelper.events.Events;

public class SlotRegistry {
    private static final Map<SlotPredicate, SlotPredicate> REGISTRY = new ConcurrentHashMap<>();

    static {
        Events.CLIENT_TICK_END.register(SlotRegistry::onClientTickEnd);
    }

    public static SlotPredicate getOrCreate(SlotPredicate prospective) {
        var canonical = REGISTRY.computeIfAbsent(prospective, key -> {
            return key;
        });
        canonical.incrementRef();
        return canonical;
    }

    public static void release(SlotPredicate canonical) {
        canonical.decrementRef();
        if (canonical.getRefCount() <= 0) {
            REGISTRY.remove(canonical, canonical);
        }
    }

    public static void onClientTickEnd(int tick) {
        if (REGISTRY.isEmpty()) return;

        for (SlotPredicate p : REGISTRY.values()) {
            p.scanInventory();
        }
    }
}