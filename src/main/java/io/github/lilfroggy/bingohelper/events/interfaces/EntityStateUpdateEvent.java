package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;

public interface EntityStateUpdateEvent {
    void onEntityStateUpdate(Entity entity, EntityRenderState state);
}