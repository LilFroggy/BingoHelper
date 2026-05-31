package io.github.lilfroggy.bingohelper.guide.step.properties.outlineEntities;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.util.EntityUtils;
import io.github.lilfroggy.bingohelper.util.entity.EntityPredicate;
import io.github.lilfroggy.bingohelper.util.render.GlowingEntities;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvent;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class OutlineEntitiesProperty extends EntityPredicate implements ClientTickEndEvent, RenderingEvent {
    private static final int DEFAULT_LINE_COLOR = RenderLib.MINECRAFT_AQUA;

    public OutlineEntitiesProperty(Line line, String type, Vec3d position, String skin) {
        super(line, type, position, skin);
    }

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
        super.getMatches().forEach(entity -> {
            GlowingEntities.add(entity, 0, 255, 255, 255);
        });
    }

    @Override
    public void render(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        switch (super.line()) {
            case NONE:
                return;
            case NEAREST:
                Entity closest = super.getClosest();
                if (closest == null) return;
                renderLine(context, closest);
                break;
            case ALL:
                super.getMatches().forEach(entity -> renderLine(context, entity));
                break;
        }
    }

    private void renderLine(WorldRenderContext context, Entity entity) {
        RenderLib.renderLineFromCursor(context, EntityUtils.getEntityMid(entity), DEFAULT_LINE_COLOR);
    }
}