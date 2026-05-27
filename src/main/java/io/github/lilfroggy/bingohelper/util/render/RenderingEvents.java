package io.github.lilfroggy.bingohelper.util.render;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class RenderingEvents {

    public static final RenderHandler<RenderingEvent> FILLED_BLOCK = new RenderHandler<>();
    public static final RenderHandler<RenderingEvent> NO_DEPTH_FILLED = new RenderHandler<>();
    public static final RenderHandler<RenderingEvent> FILLED_ENTITY = new RenderHandler<>();
    public static final RenderHandler<RenderingEvent> OUTLINE_ENTITY = new RenderHandler<>();
    public static final RenderHandler<RenderingEvent> NO_DEPTH_OUTLINE_ENTITY = new RenderHandler<>();
    public static final RenderHandler<RenderingEvent> LINE = new RenderHandler<>();

    public static void init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(RenderingEvents::filled);
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(RenderingEvents::filledNoDepth);
        WorldRenderEvents.AFTER_ENTITIES.register(RenderingEvents::entityFilled);
        WorldRenderEvents.AFTER_ENTITIES.register(RenderingEvents::entityOutline);
        WorldRenderEvents.AFTER_ENTITIES.register(RenderingEvents::entityOutlineNoDepth);
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(RenderingEvents::debugLine);
    }

    private static void filled(WorldRenderContext context) {
        WorldRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3d camera = worldState.cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_LAYER);

        FILLED_BLOCK.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }

    private static void filledNoDepth(WorldRenderContext context) {
        WorldRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3d camera = worldState.cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_LAYER_NO_DEPTH);

        NO_DEPTH_FILLED.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }

    private static void entityFilled(WorldRenderContext context) {
        WorldRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3d camera = worldState.cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_ENTITY_LAYER);

        FILLED_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }


    private static void entityOutline(WorldRenderContext context) {
        WorldRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3d camera = worldState.cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(4, true));

        OUTLINE_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }

    private static void entityOutlineNoDepth(WorldRenderContext context) {
        WorldRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3d camera = worldState.cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(4, false));

        NO_DEPTH_OUTLINE_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }

    private static void debugLine(WorldRenderContext context) {
        WorldRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(4, true));

        LINE.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }
}