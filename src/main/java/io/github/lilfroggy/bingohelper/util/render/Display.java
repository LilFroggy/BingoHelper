package io.github.lilfroggy.bingohelper.util.render;

import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;

public class Display {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static final int LINE_SPACING = 2;
    
    private MultiLineLabel text;
    private int lineCount;
    private int maxWidth;
    private int lineHeight;

    protected String string = "";
    protected double x = 10.0;
    protected double y = 10.0;
    protected float scale = 1.0f;
    protected TextAlignment align = TextAlignment.LEFT;
    protected boolean background = false;
    protected double padding = 1;

    protected double rectX;
    protected double rectY;
    protected double rectW;
    protected double rectH;

    public Display(String text) {
        setString(text);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public float scale() {
        return scale;
    }

    public int width() {
        return (int) (RenderLib.getWidth(string) * scale);
    }

    public int height() {
        return (int) (RenderLib.getHeight(string) * scale);
    }

    public float titleX() {
        float centerX = CLIENT.getWindow().getGuiScaledWidth() / 2f;
        return centerX - (this.width() / 2f);
    }

    public float titleY() {
        float centerY = CLIENT.getWindow().getGuiScaledHeight() / 2f;
        return centerY - this.height();
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
            lineHeight = CLIENT.font.lineHeight + LINE_SPACING;
        }
    }

    public Display setString(String newText) {
        if (newText == null || newText.equals(this.string)) {
            return this;
        }
    
        this.string = newText;
        this.text = null; // Mark cache as dirty so ensureTextInitialized() rebuilds it
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
        padding = background ? 5 : 1;
        return this;
    }

    public void draw(GuiGraphicsExtractor graphics, double x, double y) {
        ensureTextInitialized();

        if (text == null) return;

        this.rectX = x - (padding * scale);
        this.rectY = y - (padding * scale);
        this.rectW = (maxWidth + (padding * 2)) * scale;
        this.rectH = ((lineCount * lineHeight) - LINE_SPACING + (padding * 2)) * scale;

        if (background) drawBackground(graphics, 0x40000000);

        graphics.pose().pushMatrix();
        graphics.pose().translate((float) x, (float) y);
        graphics.pose().scale(scale, scale);

        text.visitLines(align, 0, 0, lineHeight, graphics.textRenderer());

        graphics.pose().popMatrix();
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= rectX &&
               mouseX <= rectX + rectW &&
               mouseY >= rectY &&
               mouseY <= rectY + rectH;
    }

    public void drawBackground(GuiGraphicsExtractor graphics, int color) {
        graphics.fill((int) rectX, (int) rectY, (int) (rectX + rectW), (int) (rectY + rectH), color);
    }

    public void drawOutline(GuiGraphicsExtractor graphics, int color, int thickness) {
        graphics.pose().pushMatrix();
        graphics.pose().scale(0.5f, 0.5f);
    
        int x = (int)(rectX * 2);
        int y = (int)(rectY * 2);
        int w = (int)(rectW * 2);
        int h = (int)(rectH * 2);
        int t = thickness;
    
        graphics.fill(x, y, x + w, y + t, color);
        graphics.fill(x, y + h - t, x + w, y + h, color);
        graphics.fill(x, y + t, x + t, y + h - t, color);
        graphics.fill(x + w - t, y + t, x + w, y + h - t, color);
    
        graphics.pose().popMatrix();
    }
}