package io.github.lilfroggy.bingohelper.events.interfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.renderer.MultiBufferSource;

public interface RenderWorldEvent {
    void onRenderWorld(PoseStack matrices, MultiBufferSource vertexConsumers, WorldRenderContext context);
}