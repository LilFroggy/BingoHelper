package io.github.lilfroggy.bingohelper.events.interfaces;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface RenderWorldEvent {
    void onRenderWorld(MatrixStack matrices, VertexConsumerProvider vertexConsumers, WorldRenderContext context);
}