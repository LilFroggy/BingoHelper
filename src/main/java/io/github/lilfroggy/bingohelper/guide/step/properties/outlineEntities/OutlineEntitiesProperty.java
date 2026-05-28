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

    public OutlineEntitiesProperty(String type, Vec3d position, String skin) {
        super(type, position, skin);
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
        Entity closest = super.getClosest();
        if (closest == null) return;
        Vec3d mid = EntityUtils.getEntityMid(closest);
        RenderLib.renderLineFromCursor(context, mid, DEFAULT_LINE_COLOR);
    }
}