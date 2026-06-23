package io.github.lilfroggy.bingohelper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lilfroggy.bingohelper.events.Events;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    public void onUpdateRenderState(T entity, S state, float tickProgress, CallbackInfo ci) {
        Events.ENTITY_STATE_UPDATE.invoke(listener -> listener.onEntityStateUpdate(entity, state));
    }
}