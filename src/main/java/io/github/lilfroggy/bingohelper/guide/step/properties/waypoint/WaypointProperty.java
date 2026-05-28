package io.github.lilfroggy.bingohelper.guide.step.properties.waypoint;

import java.util.List;

import io.github.lilfroggy.bingohelper.guide.step.properties.outlineEntities.OutlineEntitiesProperty;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvent;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class WaypointProperty implements RenderingEvent {
    protected static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private List<OutlineEntitiesProperty> outlineEntities;

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

    public void register(List<OutlineEntitiesProperty> outlineEntities) {
        init();
        this.outlineEntities = outlineEntities;
        RenderingEvents.LINE.register(this);
    }

    public void unregister() {
        RenderingEvents.LINE.unregister(this);
    }

    @Override
    public void render(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        WaypointEntry entry = current();

        if (outlineEntities != null && outlineEntities.stream().anyMatch(OutlineEntitiesProperty::hasMatch)) {
            return;
        }

        entry.render(context);
    }
}