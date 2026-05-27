package io.github.lilfroggy.bingohelper.util.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.github.lilfroggy.bingohelper.events.Events;
import net.minecraft.entity.Entity;

public class EntityRegistry {
    private static final Map<EntityPredicate, EntityPredicate> REGISTRY = new HashMap<>();

    static {
        Events.CLIENT_TICK_END.register(EntityRegistry::onClientTickEnd);
    }

    public static EntityPredicate getOrCreate(EntityPredicate prospective) {
        return REGISTRY.computeIfAbsent(prospective, key -> {
            key.incrementRef();
            return key;
        });
    }

    public static void release(EntityPredicate canonical) {
        canonical.decrementRef();
        if (canonical.getRefCount() <= 0) {
            REGISTRY.remove(canonical);
        }
    }

    public static Set<Entity> getEntities(EntityPredicate predicate) {
        EntityPredicate canonical = REGISTRY.get(predicate);
        return (canonical != null) ? canonical.getMatches() : Set.of();
    }

    public static void onClientTickEnd(int tick) {
        if (/*tick % 20 != 0 || */REGISTRY.isEmpty()) return;
        for (EntityPredicate p : REGISTRY.values()) {
            p.scanWorld();
        }
    }
}