package io.github.lilfroggy.bingohelper.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import io.github.lilfroggy.bingohelper.events.EntityRenderEventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "renderEntity", at = @At("HEAD"))
    private void onRenderEntity(Entity entity, double cameraX, double cameraY, double cameraZ,
                                float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                CallbackInfo ci) {
        EntityRenderEventBus.fire(entity, cameraX, cameraY, cameraZ, tickDelta, matrices, vertexConsumers);
    }
}