package io.github.lilfroggy.bingohelper.util.render;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Outline {
    // Global set of outlined entities
    private static final Set<Entity> outlinedEntities = ConcurrentHashMap.newKeySet();

    /**
     * Adds an entity to be outlined.
     */
    public static void addEntity(Entity entity) {
        outlinedEntities.add(entity);
    }

    /**
     * Removes an entity from being outlined.
     */
    public static void removeEntity(Entity entity) {
        outlinedEntities.remove(entity);
    }

    /**
     * Clears all outlined entities.
     */
    public static void clearEntities() {
        outlinedEntities.clear();
    }

    /**
     * Returns true if the entity should be outlined.
     */
    public static boolean hasEntity(Entity entity) {
        return outlinedEntities.contains(entity);
    }

    /**
     * Outlines a specific entity with the given color (for compatibility with previous usage).
     * This will add the entity to the outlined set and set the outline color if possible.
     */
    public static boolean outlineEntity(Entity entity, VertexConsumerProvider vertexConsumers, int red, int green, int blue, int alpha) {
        addEntity(entity);
        if (vertexConsumers instanceof OutlineVertexConsumerProvider outlineVertexConsumers) {
            outlineVertexConsumers.setColor(red, green, blue, alpha);
            return true;
        }
        return false;
    }
}