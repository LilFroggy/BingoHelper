package io.github.lilfroggy.bingohelper.util.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;

public interface RenderingEvent {
    void render(LevelRenderContext context, PoseStack matrixStack, VertexConsumer consumer);
}