package io.github.lilfroggy.bingohelper.events;

import net.minecraft.entity.Entity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;

public class EntityRenderEventBus {

    public interface EntityRenderListener {
        void onEntityRender(Entity entity, double cameraX, double cameraY, double cameraZ,
                            float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);
    }

    private static final EventBus<EntityRenderListener> BUS = new EventBus<>();

    public static void register(EntityRenderListener listener) {
        BUS.register(listener);
    }

    public static void unregister(EntityRenderListener listener) {
        BUS.unregister(listener);
    }

    public static void fire(Entity entity, double cameraX, double cameraY, double cameraZ,
                            float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        for (EntityRenderListener listener : BUS.getListeners()) {
            listener.onEntityRender(entity, cameraX, cameraY, cameraZ, tickDelta, matrices, vertexConsumers);
        }
    }
}