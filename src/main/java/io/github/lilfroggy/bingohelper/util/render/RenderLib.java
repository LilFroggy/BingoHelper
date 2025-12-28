package io.github.lilfroggy.bingohelper.util.render;

import io.github.lilfroggy.bingohelper.mixin.HandledScreenAccessorMixin;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Map;
import java.util.EnumSet;
import java.util.HashMap;

public class RenderLib {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final BufferAllocator ALLOCATOR = new BufferAllocator(1536);

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

    /**
     * Renders text in the world space.
     *
     * @param context The world render context
     * @param text The text to render as OrderedText
     * @param pos The position in world coordinates to render the text
     * @param scale The scale factor for the text size
     * @param yOffset The vertical offset from the position
     * @param throughWalls whether the text should be able to be seen through walls or not.
     */
    public static void renderText(WorldRenderContext context, OrderedText text, Vec3d pos, float scale, float yOffset, boolean throughWalls) {
        Matrix4f positionMatrix = new Matrix4f();
        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();
        TextRenderer textRenderer = mc.textRenderer;

        // Calculate distance from camera to text position
        double distance = cameraPos.distanceTo(pos);

        // Create a consistent scale that maintains visual size regardless of distance
        float consistentScale = (float) (distance * 0.025f * scale);
        consistentScale = Math.max(0.01f, Math.min(consistentScale, 2.0f));

        float textHeight = textRenderer.fontHeight;

        // Translate to the base position, then scale, then move up so the base is at y=0
        positionMatrix
            .translate((float) (pos.getX() - cameraPos.getX()), (float) (pos.getY() - cameraPos.getY()), (float) (pos.getZ() - cameraPos.getZ()))
            .rotate(camera.getRotation())
            .scale(consistentScale, -consistentScale, consistentScale)
            .translate(0, -textHeight, 0); // Anchor base

        float xOffset = -textRenderer.getWidth(text) / 2f;
        float anchoredYOffset = 0 + yOffset; // yOffset is now from the base

        VertexConsumerProvider.Immediate consumers = VertexConsumerProvider.immediate(ALLOCATOR);

        textRenderer.draw(text, xOffset, anchoredYOffset, 0xFFFFFFFF, true, positionMatrix, consumers, throughWalls ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        consumers.draw();
    }

    /**
     * Renders a line from the camera position to a target point in the world.
     * This method draws a line that can be seen through walls and properly handles
     * world-to-screen coordinate transformations.
     *
     * @param context The world render context containing camera, matrices, and consumers
     * @param targetPoint The 3D world position to draw the line to
     * @param colorComponents RGB color values as floats [0.0-1.0] in format [r, g, b]
     * @param alpha The transparency/alpha value [0.0-1.0] where 1.0 is fully opaque
     * @param lineWidth The thickness of the line in pixels
     */
    public static void renderLineFromCursor(WorldRenderContext context, Vec3d targetPoint,
                                            float[] colorComponents, float alpha, float lineWidth) {

        // Get camera position for coordinate system translation
        Vec3d cameraPos = context.camera().getPos();

        // Get matrix stack for transformations
        MatrixStack matrices = context.matrixStack();
        matrices.push();

        // Translate to world coordinates (subtract camera position)
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        MatrixStack.Entry matrixEntry = matrices.peek();

        // Get vertex consumer for drawing
        VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();

        // Use your custom render layer that allows lines through walls
        // Note: You'll need to replace this with your actual render layer
        RenderLayer lineLayer = RenderLayer.getLines(); // Replace with SkyblockerRenderLayers.getLinesThroughWalls(lineWidth)
        VertexConsumer vertexBuffer = consumers.getBuffer(lineLayer);

        // Calculate starting point slightly in front of camera based on camera rotation
        Vec3d startPoint = cameraPos.add(Vec3d.fromPolar(context.camera().getPitch(), context.camera().getYaw()));

        // Calculate normal vector for lighting (direction from start to end point)
        Vector3f normal = targetPoint.toVector3f()
                .sub((float) startPoint.x, (float) startPoint.y, (float) startPoint.z)
                .normalize();

        // Draw first vertex (start point - near camera)
        vertexBuffer
                .vertex(matrixEntry, (float) startPoint.x, (float) startPoint.y, (float) startPoint.z)
                .color(colorComponents[0], colorComponents[1], colorComponents[2], alpha)
                .normal(matrixEntry, normal);

        // Draw second vertex (end point - target location)
        vertexBuffer
                .vertex(matrixEntry, (float) targetPoint.getX(), (float) targetPoint.getY(), (float) targetPoint.getZ())
                .color(colorComponents[0], colorComponents[1], colorComponents[2], alpha)
                .normal(matrixEntry, normal);

        // Actually draw the line
        consumers.draw(lineLayer);

        // Restore matrix stack
        matrices.pop();
    }

    public static void renderOutline(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha, float lineWidth, boolean throughWalls) {
        //if (FrustumUtils.isVisible(minX, minY, minZ, maxX, maxY, maxZ)) {
            MatrixStack matrices = context.matrixStack();
            Vec3d camera = context.camera().getPos();

            matrices.push();
            matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());

            VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();
            RenderLayer layer = throughWalls ? BingoHelperRenderLayers.getLinesThroughWalls(lineWidth) : BingoHelperRenderLayers.getLines(lineWidth);
            VertexConsumer buffer = consumers.getBuffer(layer);

            VertexRendering.drawBox(matrices, buffer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);
            consumers.draw(layer);

            matrices.pop();
        //}
    }

    public static void renderFilled(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha, boolean throughWalls) {
        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        VertexConsumer buffer = consumers.getBuffer(throughWalls ? BingoHelperRenderLayers.FILLED_THROUGH_WALLS : BingoHelperRenderLayers.FILLED);

        VertexRendering.drawFilledBox(matrices, buffer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        matrices.pop();
    }

    public static void highlightBlock(WorldRenderContext context, Vec3d pos, float[] colorComponents, float alpha, boolean throughWalls) {
       highlightBlock(context, pos.getX(), pos.getY(), pos.getZ(), colorComponents, alpha, throughWalls);
    }

    public static void highlightBlock(WorldRenderContext context, double x, double y, double z, float[] colorComponents, float alpha, boolean throughWalls) {
        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        VertexConsumer buffer = consumers.getBuffer(throughWalls ? BingoHelperRenderLayers.FILLED_THROUGH_WALLS : BingoHelperRenderLayers.FILLED);

        VertexRendering.drawFilledBox(matrices, buffer, x-0.01, y-0.01, z-0.01, x+1.02, y+1.02, z+1.02, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        matrices.pop();
    }




    /**
     * Highlights a specific slot temporarily with a pulsing effect (alpha goes from 0.5 to 1.0 and back)
     * @param drawContext The DrawContext from the render event
     * @param slotIndex The slot to highlight
     * @param color The highlight color in ARGB format (only RGB is used, alpha is ignored)
     */
    public static void highlightSlot(DrawContext drawContext, int slotIndex, int color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) return;

        HandledScreenAccessorMixin accessor = (HandledScreenAccessorMixin) screen;

        if (slotIndex >= screen.getScreenHandler().slots.size()) return;

        Slot slot = screen.getScreenHandler().getSlot(slotIndex);
        int x = accessor.getScreenX() + slot.x;
        int y = accessor.getScreenY() + slot.y;

        // Calculate pulsing alpha (0.5 to 1.0)
        long time = System.currentTimeMillis();
        float alpha = 0.75f + 0.25f * (float)Math.cos(time * 0.004); // oscillates between 0.5 and 1.0
        int baseRGB = color & 0x00FFFFFF;
        int pulsingColor = ((int)(alpha * 255) << 24) | baseRGB;

        drawContext.fill(x, y, x + 16, y + 16, pulsingColor);
    }

    /**
     * Highlights a player inventory slot (0-35) in the current container GUI.
     * @param drawContext The DrawContext from the render event
     * @param playerInventoryIndex The index in the player's inventory (0-35)
     * @param color The highlight color in ARGB format
     */
    public static void highlightPlayerSlot(DrawContext drawContext, int playerInventoryIndex, int color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) return;

        int totalSlots = screen.getScreenHandler().slots.size();
        int containerSlots = totalSlots - 36; // 36 = player inventory slots

        // Convert player inventory index to screen slot index
        int screenSlotIndex;
        if (playerInventoryIndex < 9) {
            // Hotbar slots
            screenSlotIndex = containerSlots + 27 + playerInventoryIndex;
        } else {
            // Main inventory slots
            screenSlotIndex = containerSlots + (playerInventoryIndex - 9);
        }

        highlightSlot(drawContext, screenSlotIndex, color);
    }

    /**
     * Highlights a container slot (0-N) in the current container GUI.
     * @param drawContext The DrawContext from the render event
     * @param containerSlotIndex The index in the container (0-N)
     * @param color The highlight color in ARGB format
     */
    public static void highlightContainerSlot(DrawContext drawContext, int containerSlotIndex, int color) {
        highlightSlot(drawContext, containerSlotIndex, color);
    }










    private static final Map<Character, Formatting> formatMap = new HashMap<>();

    static {
        formatMap.put('0', Formatting.BLACK);
        formatMap.put('1', Formatting.DARK_BLUE);
        formatMap.put('2', Formatting.DARK_GREEN);
        formatMap.put('3', Formatting.DARK_AQUA);
        formatMap.put('4', Formatting.DARK_RED);
        formatMap.put('5', Formatting.DARK_PURPLE);
        formatMap.put('6', Formatting.GOLD);
        formatMap.put('7', Formatting.GRAY);
        formatMap.put('8', Formatting.DARK_GRAY);
        formatMap.put('9', Formatting.BLUE);
        formatMap.put('a', Formatting.GREEN);
        formatMap.put('b', Formatting.AQUA);
        formatMap.put('c', Formatting.RED);
        formatMap.put('d', Formatting.LIGHT_PURPLE);
        formatMap.put('e', Formatting.YELLOW);
        formatMap.put('f', Formatting.WHITE);
        formatMap.put('l', Formatting.BOLD);
        formatMap.put('n', Formatting.UNDERLINE);
        formatMap.put('o', Formatting.ITALIC);
        formatMap.put('m', Formatting.STRIKETHROUGH);
        formatMap.put('k', Formatting.OBFUSCATED);
        formatMap.put('r', Formatting.RESET);
    }

    public static void drawFormattedString(DrawContext drawContext, String input, int x, int y) {
        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer textRenderer = mc.textRenderer;
        
        int lineHeight = textRenderer.fontHeight + 2;
        int currentY = y;

        for (String line : input.split("\n")) {
            drawContext.drawTextWithShadow(textRenderer, ChatLib.replaceAmpersands(line), x, currentY, 0xFFFFFF);
            currentY += lineHeight;
        }
    }

    /**
     * Calculate the width of formatted text
     * @param input The formatted text string
     * @return The width in pixels
     */
    public static int getFormattedStringWidth(String input) {
        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer textRenderer = mc.textRenderer;
        
        int maxWidth = 0;
        for (String line : input.split("\n")) {
            int width = textRenderer.getWidth(line);
            maxWidth = Math.max(maxWidth, width);
        }
        return maxWidth;
    }
}