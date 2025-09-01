package io.github.lilfroggy.bingohelper.events;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class WorldRenderEventBus {

    public interface WorldRenderListener {
        void onWorldRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, WorldRenderContext context);
    }

    private static final EventBus<WorldRenderListener> BUS = new EventBus<>();

    static {
        WorldRenderEvents.LAST.register(context -> {
            for (WorldRenderListener listener : BUS.getListeners()) {
                listener.onWorldRender(context.matrixStack(), context.consumers(), context);
            }
        });
    }

    public static void register(WorldRenderListener listener) {
        BUS.register(listener);
    }

    public static void unregister(WorldRenderListener listener) {
        BUS.unregister(listener);
    }
}