package io.github.lilfroggy.bingohelper.guide.step.properties.waypoint;

import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class WaypointEntry {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final float DEFAULT_TEXT_SCALE = 1.0f;
    private static final int DEFAULT_LINE_COLOR = RenderLib.MINECRAFT_AQUA;
    private static final float[] DEFAULT_FILL_COLOR = {0.0f, 1.0f, 1.0f, 0.5f};
    private static final float[] DEFAULT_OUTLINE_COLOR = {0.0f, 1.0f, 1.0f, 1.0f};

    public String text;
    public Vec3d position;
    public int radius;

    private transient Vec3d center;
    private transient Box box;

    public void init() {
        center = position.add(0.5);
        box = Box.from(position);
    }

    public double distance() {
        if (CLIENT.player == null) return Double.POSITIVE_INFINITY;
        return CLIENT.player.getEntityPos().distanceTo(center);
    }

    public boolean isWithinRadius() {
        return distance() <= radius;
    }

    public void renderBox() {
        renderBox(DEFAULT_OUTLINE_COLOR, DEFAULT_FILL_COLOR);
    }

    public void renderBox(float[] strokeRGBA, float[] fillRGBA) {
        RenderLib.renderFilledAndOutline(box, strokeRGBA, fillRGBA);
    }

    public void renderText(WorldRenderContext context) {
        renderText(context, DEFAULT_TEXT_SCALE);
    }

    public void renderText(WorldRenderContext context, float scale) {
        if (text == null) return;
        RenderLib.renderText(context, text, center, scale);
    }

    public void renderLine(WorldRenderContext context) {
        renderLine(context, DEFAULT_LINE_COLOR);
    }

    public void renderLine(WorldRenderContext context, int color) {
        RenderLib.renderLineFromCursor(context, center, color);
    }

    public void render(WorldRenderContext context) {
        renderBox();
        renderLine(context);
        renderText(context);
    }
}