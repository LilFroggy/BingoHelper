package io.github.lilfroggy.bingohelper.util.render;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.EntityRenderState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.lilfroggy.bingohelper.events.Events;

public class GlowingEntities {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static final Map<Entity, Integer> GLOWING_ENTITIES = new ConcurrentHashMap<>();

    public static void init() {
        Events.ENTITY_STATE_UPDATE.register(GlowingEntities::onEntityStateUpdate);
        Events.CLIENT_TICK_START.register(tick -> clear());
    }

    public static void onEntityStateUpdate(Entity entity, EntityRenderState state) {
        Integer color = GLOWING_ENTITIES.get(entity);
        
        if (color != null) {
            state.outlineColor = color;
        } else {
            state.outlineColor = 0;
        }
    }

    public static void add(Entity entity, int red, int green, int blue, int alpha) {
        if (CLIENT.player == null) return;
        if (!CLIENT.player.canSee(entity) || !entity.isAlive()) return;
        GLOWING_ENTITIES.put(entity, ColorHelper.getArgb(alpha, red, green, blue));
    }

    public static void remove(Entity entity) {
        GLOWING_ENTITIES.remove(entity);
    }

    public static void clear() {
        GLOWING_ENTITIES.clear();
    }

    public static boolean contains(Entity entity) {
        return GLOWING_ENTITIES.containsKey(entity);
    }
}