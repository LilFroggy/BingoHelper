package io.github.lilfroggy.bingohelper.util.render;

import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayer.MultiPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Colors;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderLib {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final Camera CAMERA = CLIENT.gameRenderer.getCamera();
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

    // Shoutout Skyblocker

    /**
     * Renders text in the world space.
     *
     * @param text The text to render as OrderedText
     * @param pos The position in world coordinates to render the text
     * @param scale The scale factor for the text size
     * @param yOffset The vertical offset from the position
     * @param throughWalls whether the text should be able to be seen through walls or not.
     */
    public static void renderText(OrderedText text, Vec3d pos, float scale, float yOffset, boolean throughWalls) {
        Matrix4f positionMatrix = new Matrix4f();
        Vec3d cameraPos = CAMERA.getPos();
        TextRenderer textRenderer = CLIENT.textRenderer;

        // Calculate distance from camera to text position
        double distance = cameraPos.distanceTo(pos);

        // Create a consistent scale that maintains visual size regardless of distance
        float consistentScale = (float) (distance * 0.025f * scale);
        consistentScale = Math.max(0.01f, Math.min(consistentScale, 2.0f));

        float textHeight = textRenderer.fontHeight;

        // Translate to the base position, then scale, then move up so the base is at y=0
        positionMatrix
            .translate((float) (pos.getX() - cameraPos.getX()), (float) (pos.getY() - cameraPos.getY()), (float) (pos.getZ() - cameraPos.getZ()))
            .rotate(CAMERA.getRotation())
            .scale(consistentScale, -consistentScale, consistentScale)
            .translate(0, -textHeight, 0); // Anchor base

        float xOffset = -textRenderer.getWidth(text) / 2f;
        float anchoredYOffset = 0 + yOffset; // yOffset is now from the base

        VertexConsumerProvider.Immediate consumers = VertexConsumerProvider.immediate(ALLOCATOR);

        textRenderer.draw(text, xOffset, anchoredYOffset, 0xFFFFFFFF, true, positionMatrix, consumers, throughWalls ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        consumers.draw();
    }

    // Shoutout Skyblocker

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
    public static void renderLineFromCursor(WorldRenderContext context, Vec3d targetPoint, float[] colorComponents, float alpha, float lineWidth) {
        // Get camera position for coordinate system translation
        Vec3d cameraPos = CAMERA.getPos();

        // Get matrix stack for transformations
        MatrixStack matrices = context.matrices();
        matrices.push();

        // Translate to world coordinates (subtract camera position)
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        MatrixStack.Entry matrixEntry = matrices.peek();

        // Get vertex consumer for drawing
        VertexConsumerProvider.Immediate consumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        MultiPhase lineLayer = BingoHelperRenderLayers.getLines(lineWidth);
        VertexConsumer vertexBuffer = consumers.getBuffer(lineLayer);

        // Calculate starting point slightly in front of camera based on camera rotation
        Vec3d startPoint = cameraPos.add(Vec3d.fromPolar(CAMERA.getPitch(), CAMERA.getYaw()));

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
            MatrixStack matrices = context.matrices();
            Vec3d camera = CAMERA.getPos();

            matrices.push();
            matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());

            VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();
            RenderLayer layer = throughWalls ? BingoHelperRenderLayers.getLinesThroughWalls(lineWidth) : BingoHelperRenderLayers.getLines(lineWidth);
            VertexConsumer buffer = consumers.getBuffer(layer);

            VertexRendering.drawBox(matrices.peek(), buffer, minX, minY, minZ, maxX, maxY, maxZ, colorComponents[0], colorComponents[1], colorComponents[2], alpha);
            consumers.draw(layer);

            matrices.pop();
        //}
    }

    public static void renderFilled(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colorComponents, float alpha, boolean throughWalls) {
        MatrixStack matrices = context.matrices();
        Vec3d camera = CAMERA.getPos();

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
        MatrixStack matrices = context.matrices();
        Vec3d camera = CAMERA.getPos();

        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        VertexConsumer buffer = consumers.getBuffer(throughWalls ? BingoHelperRenderLayers.FILLED_THROUGH_WALLS : BingoHelperRenderLayers.FILLED);

        VertexRendering.drawFilledBox(matrices, buffer, x-0.01, y-0.01, z-0.01, x+1.02, y+1.02, z+1.02, colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        matrices.pop();
    }

    public static void highlightSlot(DrawContext context, Slot slot, int color) {
        if (!(CLIENT.currentScreen instanceof GenericContainerScreen)) return;

        int x = slot.x;
        int y = slot.y;

        // Calculate pulsing alpha (0.5 to 1.0)
        long time = System.currentTimeMillis();
        float alpha = 0.45f + 0.10f * (float)Math.cos(time * 0.004); // oscillates between 0.5 and 1.0
        int baseRGB = color & 0x00FFFFFF;
        int pulsingColor = ((int)(alpha * 255) << 24) | baseRGB;
        context.fill(x, y, x + 16, y + 16, pulsingColor);
    }

    public static void drawFormattedString(DrawContext drawContext, String input, int x, int y) {
        input = ChatLib.replaceAmpersands(input);
        TextRenderer textRenderer = CLIENT.textRenderer;
        
        int lineHeight = textRenderer.fontHeight + 2;
        int currentY = y;

        for (String line : input.split("\n")) {
            drawContext.drawTextWithShadow(textRenderer, line, x, currentY, Colors.WHITE);
            currentY += lineHeight;
        }
    }

    /**
     * Calculate the width of formatted text
     * @param input The formatted text string
     * @return The width in pixels
     */
    public static int getFormattedStringWidth(String input) {
        input = ChatLib.replaceAmpersands(input);
        TextRenderer textRenderer = CLIENT.textRenderer;
        
        int maxWidth = 0;
        for (String line : input.split("\n")) {
            int width = textRenderer.getWidth(line);
            maxWidth = Math.max(maxWidth, width);
        }
        return maxWidth;
    }
}