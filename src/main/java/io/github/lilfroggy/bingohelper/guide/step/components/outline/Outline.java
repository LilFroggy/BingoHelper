package io.github.lilfroggy.bingohelper.guide.step.components.outline;

import io.github.lilfroggy.bingohelper.util.EntityUtils;
import io.github.lilfroggy.bingohelper.util.entity.EntityPredicate;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvent;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class Outline extends EntityPredicate implements RenderingEvent {
    private static final int DEFAULT_LINE_COLOR = RenderLib.MINECRAFT_AQUA;

    public Outline(String type, Vec3d position, String skin) {
        super(type, position, skin);
    }

    public void init() {
        super.init();
        super.setGlowing(true);
    }

    public void register() {
        init();
        super.register();
        RenderingEvents.LINE.register(this);
    }

    public void unregister() {
        RenderingEvents.LINE.unregister(this);
        super.unregister();
    }

    public void renderLines(WorldRenderContext context) {
        renderLines(context, DEFAULT_LINE_COLOR);
    }

    public void renderLines(WorldRenderContext context, int color) {
        super.getMatches().forEach(entity -> {
            Vec3d mid = EntityUtils.getEntityMid(entity);
            RenderLib.renderLineFromCursor(context, mid, color);
        });
    }

    @Override
    public void render(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        renderLines(context);
    }
}