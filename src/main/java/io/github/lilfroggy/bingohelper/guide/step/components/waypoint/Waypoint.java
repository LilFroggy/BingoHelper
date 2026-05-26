package io.github.lilfroggy.bingohelper.guide.step.components.waypoint;

import io.github.lilfroggy.bingohelper.guide.step.components.outline.Outline;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvent;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class Waypoint implements RenderingEvent {
    protected static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private Outline outlineEntity;

    public WaypointEntry[] list;
    public int index;

    public void init() {
        for (WaypointEntry entry : list) {
            entry.init();
        }
    }

    public void reset() {
        index = 0;
    }

    public WaypointEntry current() {
        if (index < 0) index = 0;
        if (index >= list.length) index = list.length - 1;
        WaypointEntry current = this.list[this.index];
        if (current.isWithinRadius()) advance();
        return this.list[this.index];
    }

    public boolean isOnLast() {
        if (list == null || list.length == 0) return true;
        return this.index == this.list.length - 1;
    }

    public void advance() {
        if (isOnLast()) return;
        this.index++;
    }

    public void register(Outline outlineEntity) {
        init();
        this.outlineEntity = outlineEntity;
        RenderingEvents.LINE.register(this);
    }

    public void unregister() {
        RenderingEvents.LINE.unregister(this);
    }

    @Override
    public void render(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        WaypointEntry entry = current();

        if (outlineEntity != null && outlineEntity.hasMatch()) {
            return;
        }

        entry.render(context);
    }
}