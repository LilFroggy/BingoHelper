package io.github.lilfroggy.bingohelper.util.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class AnimatedTitle {
    private Display display;
    private long startTime;
    private long duration;
    private long delay;
    private float startX, startY, endX, endY, startScale, endScale;

    public AnimatedTitle(Display display, float startX, float startY, float endX, float endY, float startScale, float endScale, long duration, long delay) {
        this.display = display;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.startScale = startScale;
        this.endScale = endScale;
        this.duration = duration;
        this.delay = delay;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime > (duration + delay);
    }

    public void render(GuiGraphicsExtractor graphics) {
        if (isFinished()) return;

        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed < delay) {
            display.setScale(startScale);
            display.draw(graphics, (int) startX, (int) startY);
            return;
        }

        float progress = (float) (elapsed - delay) / duration;
        
        progress = Math.min(1.0f, progress);
        
        float t = 1.0f - (float) Math.pow(1.0f - progress, 3);

        float currentX = startX + (endX - startX) * t;
        float currentY = startY + (endY - startY) * t;
        float currentScale = startScale + (endScale - startScale) * t;

        display.setScale(currentScale);
        display.draw(graphics, (int) currentX, (int) currentY);
    }
}