package io.github.lilfroggy.bingohelper.util.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.Vec3;

public class RenderingEvents {

    public static final RenderHandler<RenderingEvent> FILLED_BLOCK = new RenderHandler<>();
    public static final RenderHandler<RenderingEvent> NO_DEPTH_FILLED = new RenderHandler<>();
    public static final RenderHandler<RenderingEvent> FILLED_ENTITY = new RenderHandler<>();
    public static final RenderHandler<RenderingEvent> OUTLINE_ENTITY = new RenderHandler<>();
    public static final RenderHandler<RenderingEvent> NO_DEPTH_OUTLINE_ENTITY = new RenderHandler<>();
    public static final RenderHandler<RenderingEvent> LINE = new RenderHandler<>();

    public static void init() {
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(RenderingEvents::filled);
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(RenderingEvents::filledNoDepth);
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(RenderingEvents::debugLine);
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(RenderingEvents::entityFilled);
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(RenderingEvents::entityOutline);
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(RenderingEvents::entityOutlineNoDepth);
    }

    private static void filled(LevelRenderContext context) {
        LevelRenderState worldState = context.levelState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.poseStack();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.bufferSource();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_LAYER);

        FILLED_BLOCK.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }

    private static void filledNoDepth(LevelRenderContext context) {
        LevelRenderState worldState = context.levelState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.poseStack();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.bufferSource();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_LAYER_NO_DEPTH);

        NO_DEPTH_FILLED.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }

    private static void entityFilled(LevelRenderContext context) {
        LevelRenderState worldState = context.levelState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.poseStack();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.bufferSource();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_ENTITY_LAYER);

        FILLED_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }


    private static void entityOutline(LevelRenderContext context) {
        LevelRenderState worldState = context.levelState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.poseStack();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.bufferSource();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(4, true));

        OUTLINE_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }

    private static void entityOutlineNoDepth(LevelRenderContext context) {
        LevelRenderState worldState = context.levelState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.poseStack();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.bufferSource();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(4, false));

        NO_DEPTH_OUTLINE_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }

    private static void debugLine(LevelRenderContext context) {
        LevelRenderState worldState = context.levelState();
        if (worldState == null) return;
        Vec3 camera = context.levelState().cameraRenderState.pos;
        PoseStack matrices = context.poseStack();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.bufferSource();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(4, true));

        LINE.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }
}