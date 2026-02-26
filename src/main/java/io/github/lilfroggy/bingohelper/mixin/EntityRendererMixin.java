package io.github.lilfroggy.bingohelper.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lilfroggy.bingohelper.events.EntityStateUpdateEventBus;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    
    @Inject(method = "updateRenderState", at = @At("TAIL"))
    public void onUpdateRenderState(T entity, S state, float tickProgress, CallbackInfo ci) {
        EntityStateUpdateEventBus.fire(entity, state);
    }
}