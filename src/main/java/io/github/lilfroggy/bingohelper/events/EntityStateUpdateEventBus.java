package io.github.lilfroggy.bingohelper.events;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class EntityStateUpdateEventBus {
    public interface EntityStateUpdateListener {
        void onUpdateEntityState(Entity entity, EntityRenderState state);
    }

    private static final EventBus<EntityStateUpdateListener> BUS = new EventBus<>();

    public static void register(EntityStateUpdateListener listener) {
        BUS.register(listener);
    }

    public static void unregister(EntityStateUpdateListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(Entity entity, EntityRenderState state) {
        for (EntityStateUpdateListener listener : BUS.getListeners()) {
            listener.onUpdateEntityState(entity, state);
        }
    }
}