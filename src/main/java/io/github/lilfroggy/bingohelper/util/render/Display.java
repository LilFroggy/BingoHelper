package io.github.lilfroggy.bingohelper.util.render;

import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class Display {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static final int LINE_SPACING = 2;
    private static final int FONT_HEIGHT_FALLBACK = 9;
    
    private MultilineText text;
    private int lineCount;
    private int maxWidth;
    private int lineHeight;

    private String string = "";
    private float scale = 1.0f;
    private Alignment align = Alignment.LEFT;
    private boolean background = false;
    private int padding = 4;

    public Display(String text) {
        setString(text);
    }

    private void ensureTextInitialized() {
        if (this.text == null && CLIENT.textRenderer != null) {
            this.text = MultilineText.create(
                CLIENT.textRenderer,
                Text.literal(ChatLib.replaceAmpersands(string)),
                200
            );
            lineCount = this.text.getLineCount();
            maxWidth = this.text.getMaxWidth();
            lineHeight = CLIENT.textRenderer == null ? FONT_HEIGHT_FALLBACK + LINE_SPACING : CLIENT.textRenderer.fontHeight + LINE_SPACING;
        }
    }

    public Display setString(String newText) {
        if (newText == null || newText.equals(this.string)) {
            return this;
        }
    
        this.string = newText;
        this.text = null; // Mark the cache as dirty so ensureTextInitialized() rebuilds it
        return this;
    }

    public Display setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public Display setAlign(Alignment align) {
        this.align = align;
        return this;
    }

    public Display setBackground(boolean background) {
        this.background = background;
        return this;
    }

    public void draw(DrawContext context, int x, int y) {
        ensureTextInitialized();

        if (text == null) return;

        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scale, scale);

        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);

        if (background) {
            context.fill(
                scaledX - padding,
                scaledY - padding, 
                scaledX + maxWidth + padding,
                scaledY + (lineCount * lineHeight) - LINE_SPACING + padding, 
                0xBF000000
            );
        }

        text.draw(align, scaledX, scaledY, lineHeight, context.getTextConsumer());

        context.getMatrices().popMatrix();
    }
}