package io.github.lilfroggy.bingohelper.util.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

    public static void renderText(WorldRenderContext context, String text, Vec3 pos, float scale) {
        renderText(context, Component.literal(ChatLib.replaceAmpersands(text)), pos.x, pos.y, pos.z, scale);
    }
    
    public static void renderText(WorldRenderContext context, Component text, Vec3 pos, float scale) {
        renderText(context, text, pos.x, pos.y, pos.z, scale);
    }

    public static void renderText(WorldRenderContext context, String text, double x, double y, double z, float scale) {
        renderText(context, Component.literal(ChatLib.replaceAmpersands(text)), x, y, z, scale);
    }

    public static void renderText(WorldRenderContext context, Component text, double x, double y, double z, float scale) {
        Minecraft client = Minecraft.getInstance();
        Font textRenderer = client.font;
        if (client.player == null) return;

        PoseStack matrices = context.matrices();
    
        // 1. Get camera position from the modern context
        Vec3 cameraPos = context.worldState().cameraRenderState.pos;
    
        double distance = Math.sqrt(cameraPos.distanceToSqr(x, y, z));
    
        // 3. Replicate your old consistent scale logic (preserves visual size at a distance)
        float consistentScale = (float) (distance * TEXT_SCALE * scale);
        consistentScale = Math.max(0.01f, consistentScale);
    
        matrices.pushPose();
        
        // 4. Translate to the target coordinates relative to the camera
        matrices.translate(x, y, z);
        
        // 5. Face the camera
        matrices.mulPose(context.worldState().cameraRenderState.orientation);
        
        // 6. Apply distance scale (flipping Y like your old matrix did)
        matrices.scale(consistentScale, -consistentScale, consistentScale);
    
        // 7. Anchor the base: Push the text up by its own height so (x, y, z) is the center-bottom
        float textHeight = textRenderer.lineHeight;
        matrices.translate(0, -textHeight, 0);
    
        // 8. Center alignment (X-axis offset)
        float halfWidth = textRenderer.width(text) / 2f;
    
        // 9. Submit text to the command queue (using modern max light coordinates)
        context.commandQueue().submitText(
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

    public static void renderLineFromCursor(WorldRenderContext context, Vec3 pos, int color) {
        renderLineFromCursor(context, pos.x, pos.y, pos.z, color, LINE_THICKNESS);
    }

    public static void renderLineFromCursor(WorldRenderContext context, Vec3 pos, int color, float width) {
        renderLineFromCursor(context, pos.x, pos.y, pos.z, color, width);
    }

    public static void renderLineFromCursor(WorldRenderContext context, double x, double y, double z, int color, float width) {
        Vec3 cameraPos = context.worldState().cameraRenderState.pos;
        Vector3f lookAt = new Vector3f(0, 0, -1f).rotate(context.worldState().cameraRenderState.orientation);
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

    public static void highlightSlot(GuiGraphics graphics, Slot slot, int color) {
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

    public static int getFormattedStringWidth(String input) {
        input = ChatLib.replaceAmpersands(input);
        Font textRenderer = CLIENT.font;
        
        int maxWidth = 0;
        for (String line : input.split("\n")) {
            int width = textRenderer.width(line);
            maxWidth = Math.max(maxWidth, width);
        }
        return maxWidth;
    }
}