package io.github.lilfroggy.bingohelper.hud;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import io.github.lilfroggy.bingohelper.Client;
import io.github.lilfroggy.bingohelper.util.PersistentData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class HudManager extends Screen {
    public static final HudManager INSTANCE = new HudManager();
    public static final PersistentData data = new PersistentData("config/bingohelper/huds.json", "{}");
    private final Set<HudDisplay> huds = new HashSet<>();

    private HudDisplay selectedHud = null;
    private boolean isOpen = false;

    boolean mouseDown = false;
    double lastMouseX = 0.0;
    double lastMouseY = 0.0;

    double cumDragX = 0.0;
    double cumDragY = 0.0;
    double startDragX = 0.0;
    double startDragY = 0.0;

    private HudManager() {
        super(Component.literal("BingoHelper.HudManager"));
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) updateSelected();

        mouseDown = true;

        cumDragX = 0.0;
        cumDragY = 0.0;

        startDragX = selectedHud != null ? selectedHud.x() : 0.0;
        startDragY = selectedHud != null ? selectedHud.y() : 0.0;

        return false;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        mouseDown = false;
        cumDragX = 0.0;
        cumDragY = 0.0;
        startDragX = 0.0;
        startDragY = 0.0;

        return false;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (event.button() != 0) return false;

        cumDragX += dx;
        cumDragY += dy;

        if (selectedHud != null) selectedHud.onMouseDrag(dx, dy);

        return false;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (selectedHud != null) selectedHud.onMouseScroll(scrollY);

        return false;
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) return super.keyPressed(event);

        if (selectedHud != null) selectedHud.onKeyPress(event.key());

        return false;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        for (HudDisplay hud : huds) {
            if (!hud.isEnabled()) continue;
            hud.drawExample(graphics, hud == selectedHud);
        }
    }

    @Override
    protected void init() {
        isOpen = true;
    }

    @Override
    public void removed() {
        isOpen = false;
        selectedHud = null;
        mouseDown = false;
        for (HudDisplay hud : huds) {
            hud.save();
        }
        data.save();
    }

    private void updateSelected() {
        if (mouseDown) return;

        selectedHud = huds.stream()
            .filter(hud -> hud.isHovered(lastMouseX, lastMouseY))
            .min(Comparator.comparingInt(hud -> {
                int sizeMetric = Math.min(hud.width(), hud.height());
                int weight = hud.isEnabled() ? 0 : 1_000_000;
                
                return sizeMetric + weight;
            }))
            .orElse(null);
    }

    public static void addHud(HudDisplay hud) {
        INSTANCE.huds.add(hud);
    }

    public static void open() {
        Client.MINECRAFT.schedule(() -> Client.MINECRAFT.setScreenAndShow(INSTANCE));
    }

    public static boolean isOpen() {
        return INSTANCE.isOpen;
    }

    public HudManager getInstance() {
        return INSTANCE;
    }
}