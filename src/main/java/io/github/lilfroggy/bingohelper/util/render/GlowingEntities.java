package io.github.lilfroggy.bingohelper.util.render;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.client.render.entity.state.EntityRenderState;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GlowingEntities {
    private static final Set<Entity> GLOWING_ENTITIES = ConcurrentHashMap.newKeySet();
    
    /**
     * Call from EntityStateUpdateEventBus
     */
    public static void add(Entity entity, EntityRenderState state, int red, int green, int blue, int alpha) {
        GLOWING_ENTITIES.add(entity);
        state.outlineColor = ColorHelper.getArgb(alpha, red, green, blue);
    }

    public static void remove(Entity entity) {
        GLOWING_ENTITIES.remove(entity);
    }

    public static void clear() {
        GLOWING_ENTITIES.clear();
    }

    public static boolean contains(Entity entity) {
        return GLOWING_ENTITIES.contains(entity);
    }
}