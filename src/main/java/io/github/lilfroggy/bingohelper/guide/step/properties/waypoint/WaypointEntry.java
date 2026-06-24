package io.github.lilfroggy.bingohelper.guide.step.properties.waypoint;

import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WaypointEntry {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private static final float DEFAULT_TEXT_SCALE = 1.0f;
    private static final int DEFAULT_LINE_COLOR = RenderLib.MINECRAFT_AQUA;
    private static final float[] DEFAULT_FILL_COLOR = {0.0f, 1.0f, 1.0f, 0.5f};
    private static final float[] DEFAULT_OUTLINE_COLOR = {0.0f, 1.0f, 1.0f, 1.0f};

    public String text;
    public Vec3 position;
    public int radius;

    private transient Vec3 center;
    private transient AABB box;

    public void init() {
        center = position.add(0.5);
        box = AABB.unitCubeFromLowerCorner(position);
    }

    public double distance() {
        if (CLIENT.player == null) return Double.POSITIVE_INFINITY;
        return CLIENT.player.position().distanceTo(center);
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

    public void renderText(LevelRenderContext context) {
        renderText(context, DEFAULT_TEXT_SCALE);
    }

    public void renderText(LevelRenderContext context, float scale) {
        if (text == null) return;
        RenderLib.renderText(context, text, center, scale);
    }

    public void renderLine(LevelRenderContext context) {
        renderLine(context, DEFAULT_LINE_COLOR);
    }

    public void renderLine(LevelRenderContext context, int color) {
        RenderLib.renderLineFromCursor(context, center, color);
    }

    public void render(LevelRenderContext context) {
        renderBox();
        renderLine(context);
        renderText(context);
    }
}