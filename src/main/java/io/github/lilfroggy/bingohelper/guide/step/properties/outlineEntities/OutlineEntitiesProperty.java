package io.github.lilfroggy.bingohelper.guide.step.properties.outlineEntities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.util.EntityUtils;
import io.github.lilfroggy.bingohelper.util.entity.EntityPredicate;
import io.github.lilfroggy.bingohelper.util.render.GlowingEntities;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvent;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.world.entity.Entity;

public class OutlineEntitiesProperty extends EntityPredicate implements ClientTickEndEvent, RenderingEvent {
    private static final int DEFAULT_LINE_COLOR = RenderLib.MINECRAFT_AQUA;

    public void register() {
        super.register();
        Events.CLIENT_TICK_END.register(this);
        RenderingEvents.LINE.register(this);
    }

    public void unregister() {
        RenderingEvents.LINE.unregister(this);
        Events.CLIENT_TICK_END.unregister(this);
        super.unregister();
    }

    @Override
    public void onClientTickEnd(int tick) {
        getMatches().forEach(entity -> {
            GlowingEntities.add(entity, 0, 255, 255, 255);
        });
    }

    @Override
    public void render(LevelRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        switch (super.line()) {
            case NONE: return;
            case NEAREST: renderLine(context, getClosest());
            case ALL: super.getMatches().forEach(entity -> renderLine(context, entity));
        }
    }

    private void renderLine(LevelRenderContext context, Entity entity) {
        if (entity == null) return;
        RenderLib.renderLineFromCursor(context, EntityUtils.getEntityMid(entity), DEFAULT_LINE_COLOR);
    }
}