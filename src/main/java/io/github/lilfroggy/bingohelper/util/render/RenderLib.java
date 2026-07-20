package io.github.lilfroggy.bingohelper.util.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderLib {
    private static final float LINE_THICKNESS = 3.0f;
    private static final float TEXT_SCALE = 0.005f;

    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static final int MINECRAFT_BLACK = 0xFF000000;
    public static final int MINECRAFT_DARK_BLUE = 0xFF0000AA;
    public static final int MINECRAFT_DARK_GREEN = 0xFF00AA00;
    public static final int MINECRAFT_DARK_AQUA = 0xFF00AAAA;
    public static final int MINECRAFT_DARK_RED = 0xFFAA0000;
    public static final int MINECRAFT_DARK_PURPLE = 0xFFAA00AA;
    public static final int MINECRAFT_GOLD = 0xFFFFAA00;
    public static final int MINECRAFT_GRAY = 0xFFAAAAAA;
    public static final int MINECRAFT_DARK_GRAY = 0xFF555555;
    public static final int MINECRAFT_BLUE = 0xFF5555FF;
    public static final int MINECRAFT_GREEN = 0xFF55FF55;
    public static final int MINECRAFT_AQUA = 0xFF55FFFF;
    public static final int MINECRAFT_RED = 0xFFFF5555;
    public static final int MINECRAFT_LIGHT_PURPLE = 0xFFFF55FF;
    public static final int MINECRAFT_YELLOW = 0xFFFFFF55;
    public static final int MINECRAFT_WHITE = 0xFFFFFFFF;

    // Shoutout Blade-Addons

    public static void renderText(LevelRenderContext context, String text, Vec3 pos, float scale) {
        renderText(context, Component.literal(ChatLib.replaceAmpersands(text)), pos.x, pos.y, pos.z, scale);
    }
    
    public static void renderText(LevelRenderContext context, Component text, Vec3 pos, float scale) {
        renderText(context, text, pos.x, pos.y, pos.z, scale);
    }

    public static void renderText(LevelRenderContext context, String text, double x, double y, double z, float scale) {
        renderText(context, Component.literal(ChatLib.replaceAmpersands(text)), x, y, z, scale);
    }

    public static void renderText(LevelRenderContext context, Component text, double x, double y, double z, float scale) {
        Minecraft client = Minecraft.getInstance();
        Font textRenderer = client.font;
        if (client.player == null) return;

        PoseStack matrices = context.poseStack();
    
        // 1. Get camera position from the modern context
        Vec3 cameraPos = context.levelState().cameraRenderState.pos;
    
        double distance = Math.sqrt(cameraPos.distanceToSqr(x, y, z));
    
        // 3. Replicate your old consistent scale logic (preserves visual size at a distance)
        float consistentScale = (float) (distance * TEXT_SCALE * scale);
        consistentScale = Math.max(0.01f, consistentScale);
    
        matrices.pushPose();
        
        // 4. Translate to the target coordinates relative to the camera
        matrices.translate(x, y, z);
        
        // 5. Face the camera
        matrices.mulPose(context.levelState().cameraRenderState.orientation);
        
        // 6. Apply distance scale (flipping Y like your old matrix did)
        matrices.scale(consistentScale, -consistentScale, consistentScale);
    
        // 7. Anchor the base: Push the text up by its own height so (x, y, z) is the center-bottom
        float textHeight = textRenderer.lineHeight;
        matrices.translate(0, -textHeight, 0);
    
        // 8. Center alignment (X-axis offset)
        float halfWidth = textRenderer.width(text) / 2f;
    
        // 9. Submit text to the command queue (using modern max light coordinates)
        context.submitNodeCollector().submitText(
            matrices, 
            -halfWidth,                       // Centered horizontally
            0,                                // 0 is now cleanly anchored at the bottom
            text.getVisualOrderText(), 
            true,                             // Drop shadow
            Font.DisplayMode.SEE_THROUGH, 
            15728880,                         // Max light (0xF000F0)
            0xFFFFFFFF,                       // White text color
            0, 
            0
        );
        
        matrices.popPose();
    }

    public static void renderLineFromCursor(LevelRenderContext context, Vec3 pos, int color) {
        renderLineFromCursor(context, pos.x, pos.y, pos.z, color, LINE_THICKNESS);
    }

    public static void renderLineFromCursor(LevelRenderContext context, Vec3 pos, int color, float width) {
        renderLineFromCursor(context, pos.x, pos.y, pos.z, color, width);
    }

    public static void renderLineFromCursor(LevelRenderContext context, double x, double y, double z, int color, float width) {
        Vec3 cameraPos = context.levelState().cameraRenderState.pos;
        Vector3f lookAt = new Vector3f(0, 0, -1f).rotate(context.levelState().cameraRenderState.orientation);
        Vec3 startPos = cameraPos.add(lookAt.x * 0.1, lookAt.y * 0.1, lookAt.z * 0.1);
        Gizmos.line(startPos, new Vec3(x, y, z), color, width);
    }

    public static void renderOutline(AABB box, float[] rgba) {
        renderOutline(box, rgba, LINE_THICKNESS);
    }

    public static void renderOutline(AABB box, float[] rgba, float width) {
        int stroke = ARGB.colorFromFloat(rgba[3], rgba[0], rgba[1], rgba[2]);
        Gizmos.cuboid(box, GizmoStyle.stroke(stroke, width));
    }

    public static void renderFilled(AABB box, float[] rgba) {
        Gizmos.cuboid(box, GizmoStyle.fill(ARGB.colorFromFloat(rgba[3], rgba[0], rgba[1], rgba[2])));
    }

    public static void renderFilledAndOutline(AABB box, float[] strokeRGBA, float[] fillRGBA) {
        renderFilledAndOutline(box, strokeRGBA, fillRGBA, LINE_THICKNESS);
    }

    public static void renderFilledAndOutline(AABB box, float[] strokeRGBA, float[] fillRGBA, float outlineWidth) {
        int stroke = ARGB.colorFromFloat(strokeRGBA[3], strokeRGBA[0], strokeRGBA[1], strokeRGBA[2]);
        int fill = ARGB.colorFromFloat(fillRGBA[3], fillRGBA[0], fillRGBA[1], fillRGBA[2]);
        Gizmos.cuboid(box, GizmoStyle.strokeAndFill(stroke, outlineWidth, fill));
    }

    public static void highlightSlot(GuiGraphicsExtractor graphics, Slot slot, int color) {
        int x = slot.x;
        int y = slot.y;

        long time = System.currentTimeMillis();
        float alpha = 0.45f + 0.10f * (float) Math.cos(time * 0.004); // oscillates between 0.5 and 1.0
        int baseRGB = color & 0x00FFFFFF;
        int pulsingColor = ((int) (alpha * 255) << 24) | baseRGB;
        graphics.fill(x, y, x + 16, y + 16, pulsingColor);

        int borderColor = 0xFF000000 | baseRGB; // 0xFF alpha (opaque) + base color
    
        graphics.fill(x - 1, y - 1, x + 17, y, borderColor);
        graphics.fill(x - 1, y + 16, x + 17, y + 17, borderColor);
        graphics.fill(x - 1, y, x, y + 16, borderColor);
        graphics.fill(x + 16, y, x + 17, y + 16, borderColor);
    }

    public static int getWidth(String input) {
        input = ChatLib.replaceAmpersands(input);
        
        int maxWidth = 0;
        for (String line : input.split("\n")) {
            int width = CLIENT.font.width(line);
            maxWidth = Math.max(maxWidth, width);
        }
        return maxWidth;
    }

    public static int getHeight(String input) {
        return input.split("\n").length * (CLIENT.font.lineHeight + Display.LINE_SPACING);
    }

    private static void quad(Matrix4f matrix,
            VertexConsumer consumer,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3,
            double x4, double y4, double z4,

            float r, float g, float b, float a) {

        consumer.addVertex(matrix, (float) x1, (float) y1, (float) z1)
        .setColor(r, g, b, a)
        .setNormal(0, 1, 0);

        consumer.addVertex(matrix, (float) x2, (float) y2, (float) z2)
        .setColor(r, g, b, a)
        .setNormal(0, 1, 0);

        consumer.addVertex(matrix, (float) x3, (float) y3, (float) z3)
        .setColor(r, g, b, a)
        .setNormal(0, 1, 0);

        consumer.addVertex(matrix, (float) x4, (float) y4, (float) z4)
        .setColor(r, g, b, a)
        .setNormal(0, 1, 0);

    }

    public static void renderFilledBox(PoseStack matrices, VertexConsumer consumer, AABB box, float[] rgba) {

        if (rgba[3] == 0) return;
        float r = rgba[0];
        float g = rgba[1];
        float b = rgba[2];
        float a = rgba[3];

        Matrix4f matrix = matrices.last().pose();

        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        quad(matrix, consumer, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, r, g, b, a);
        quad(matrix, consumer, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, r, g, b, a);

        quad(matrix, consumer, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, minX, minY, maxZ, r, g, b, a);
        quad(matrix, consumer, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, minX, minY, minZ, r, g, b, a);

        quad(matrix, consumer, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, r, g, b, a);
        quad(matrix, consumer, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

    }
}