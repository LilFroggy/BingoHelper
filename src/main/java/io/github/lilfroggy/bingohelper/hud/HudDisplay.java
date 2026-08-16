package io.github.lilfroggy.bingohelper.hud;

import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.Window;

import io.github.lilfroggy.bingohelper.Client;
import io.github.lilfroggy.bingohelper.util.render.Display;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class HudDisplay extends Display {

    Supplier<Boolean> criteria;
    String configName;

    public HudDisplay(String text, String configName, Supplier<Boolean> criteria) {
        super(text);
        this.criteria = criteria;
        this.configName = configName;
        load();
        HudManager.addHud(this);
    }

    public void onMouseScroll(double dir) {
        boolean isCtrlDown = Client.MINECRAFT.hasControlDown();
        boolean isShiftDown = Client.MINECRAFT.hasShiftDown();
        final float INCREMENT = isCtrlDown ? 0.2f : isShiftDown ? 0.1f : 0.02f;
        float delta = INCREMENT * Math.signum((float) dir);
        scale = Math.max(scale + delta, 0.1f);
        x = coerceX(x);
        y = coerceY(y);
    }

    public void onMouseDrag(double dx, double dy) {
        x = coerceX(x + dx);
        y = coerceY(y + dy);
    }

    public void onKeyPress(int keyCode) {
        boolean isCtrlDown = Client.MINECRAFT.hasControlDown();
        boolean isShiftDown = Client.MINECRAFT.hasShiftDown();
        final double INCREMENT = isCtrlDown ? 10.0 : isShiftDown ? 5.0 : 1.0;
        double dx = 0.0;
        double dy = 0.0;
        switch (keyCode) {
            case GLFW.GLFW_KEY_R -> setDefaultValues();
            case GLFW.GLFW_KEY_B -> setBackground(!background);
            case GLFW.GLFW_KEY_LEFT -> dx = -INCREMENT;
            case GLFW.GLFW_KEY_RIGHT -> dx = INCREMENT;
            case GLFW.GLFW_KEY_UP -> dy = -INCREMENT;
            case GLFW.GLFW_KEY_DOWN -> dy = INCREMENT;
            case GLFW.GLFW_KEY_MINUS -> onMouseScroll(-1.0);
            case GLFW.GLFW_KEY_EQUAL -> onMouseScroll(+1.0);
        }

        x = coerceX(x + dx);
        y = coerceY(y + dy);
    }

    private double coerceX(double v) {
        double maxX = getWindow().getGuiScaledWidth() - width() - margin();
        return Math.max(margin(), Math.min(maxX, v));
    }

    private double coerceY(double v) {
        double maxY = getWindow().getGuiScaledHeight() - height() - margin() + 1;
        return Math.max(margin(), Math.min(maxY, v));
    }

    public double margin() {
        return padding * scale;
    }

    public Window getWindow() {
        return Client.MINECRAFT.getWindow();
    }

    public boolean isEnabled() {
        return criteria == null || criteria.get();
    }

    public void draw(GuiGraphicsExtractor graphics) {
        if (HudManager.isOpen()) return;
        if (!isEnabled()) return;

        setScale(scale);
        setBackground(background);
        setAlign(align);
        draw(graphics, x, y);
    }

    public void drawExample(GuiGraphicsExtractor graphics, boolean selected) {
        setScale(scale);
        setAlign(align);
        int outline = selected ? 0xFFFFFF00 : 0xFFFFFFFF;
        int thickness = selected ? 2 : 1;
        draw(graphics, x, y);
        drawOutline(graphics, outline, thickness);
    }

    public void load() {
        var data = HudManager.data.getObject(configName);
        if (data == null) return;
        x = data.getDouble("x");
        y = data.getDouble("y");
        scale = data.getFloat("scale");
        background = data.getBoolean("background");
    }

    public void save() {
        var data = HudManager.data.getOrCreateObject(configName);
        data.set("x", x);
        data.set("y", y);
        data.set("scale", scale);
        data.set("background", background);
    }

    public void setDefaultValues() {
        x = 10.0;
        y = 10.0;
        scale = 1f;
        setBackground(false);
    }
}