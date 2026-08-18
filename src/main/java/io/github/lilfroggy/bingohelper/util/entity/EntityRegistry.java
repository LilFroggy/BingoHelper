package io.github.lilfroggy.bingohelper.util.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.lilfroggy.bingohelper.events.Events;

public class EntityRegistry {
    private static final Map<EntityPredicate, EntityPredicate> REGISTRY = new ConcurrentHashMap<>();

    static {
        Events.CLIENT_TICK_END.register(EntityRegistry::onClientTickEnd);
    }

    public static EntityPredicate getOrCreate(EntityPredicate prospective) {
        var canonical = REGISTRY.computeIfAbsent(prospective, key -> {
            return key;
        });
        canonical.incrementRef();
        return canonical;
    }

    public static void release(EntityPredicate canonical) {
        canonical.decrementRef();
        if (canonical.getRefCount() <= 0) {
            REGISTRY.remove(canonical, canonical);
        }
    }

    public static void onClientTickEnd(int tick) {
        if (REGISTRY.isEmpty()) return;

        for (EntityPredicate p : REGISTRY.values()) {
            p.scanWorld();
        }
    }
}