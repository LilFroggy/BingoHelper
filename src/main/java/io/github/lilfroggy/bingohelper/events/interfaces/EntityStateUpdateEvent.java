package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public interface EntityStateUpdateEvent {
    void onEntityStateUpdate(Entity entity, EntityRenderState state);
}