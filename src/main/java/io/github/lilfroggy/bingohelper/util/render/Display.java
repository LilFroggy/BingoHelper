package io.github.lilfroggy.bingohelper.util.render;

import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;

public class Display {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private static final int LINE_SPACING = 2;
    private static final int FONT_HEIGHT_FALLBACK = 9;
    
    private MultiLineLabel text;
    private int lineCount;
    private int maxWidth;
    private int lineHeight;

    private String string = "";
    private float scale = 1.0f;
    private TextAlignment align = TextAlignment.LEFT;
    private boolean background = false;
    private int padding = 4;

    public Display(String text) {
        setString(text);
    }

    private void ensureTextInitialized() {
        if (this.text == null && CLIENT.font != null) {
            this.text = MultiLineLabel.create(
                CLIENT.font,
                Component.literal(ChatLib.replaceAmpersands(string)),
                Integer.MAX_VALUE
            );
            lineCount = this.text.getLineCount();
            maxWidth = this.text.getWidth();
            lineHeight = CLIENT.font == null ? FONT_HEIGHT_FALLBACK + LINE_SPACING : CLIENT.font.lineHeight + LINE_SPACING;
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

    public Display setAlign(TextAlignment align) {
        this.align = align;
        return this;
    }

    public Display setBackground(boolean background) {
        this.background = background;
        return this;
    }

    public void draw(GuiGraphicsExtractor graphics, int x, int y) {
        ensureTextInitialized();

        if (text == null) return;

        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);

        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);

        if (background) {
            graphics.fill(
                scaledX - padding,
                scaledY - padding, 
                scaledX + maxWidth + padding,
                scaledY + (lineCount * lineHeight) - LINE_SPACING + padding, 
                0xBF000000
            );
        }

        text.visitLines(align, scaledX, scaledY, lineHeight, graphics.textRenderer());

        graphics.pose().popMatrix();
    }
}