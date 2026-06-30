package io.github.lilfroggy.bingohelper.guide.step.properties.async;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderHudEvent;
import io.github.lilfroggy.bingohelper.guide.ActiveSteps;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.DwarvenEvents;
import io.github.lilfroggy.bingohelper.util.entity.EntityPredicate;
import io.github.lilfroggy.bingohelper.util.render.AnimatedTitle;
import io.github.lilfroggy.bingohelper.util.render.Display;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;

public class AsyncRequirements implements RenderHudEvent {
    private transient static final Minecraft CLIENT = Minecraft.getInstance();

    public Step step;
    private transient AnimatedTitle activeTitle;
    private transient Display display;
    private transient final float NOTIFICATION_SCALE = 5;
    private transient boolean isNotifying = false;

    public String notification;
    public EntityPredicate entity;
    public String dwarvenEvent;

    private transient boolean wasMet = false;
    private transient long lastNotified = 0;
    private transient static final long COOLDOWN_MS = 10000;

    public void register(Step step) {
        this.step = step;
        if (notification != null) Events.RENDER_HUD.register(this);
        if (entity != null) entity.register();
    }

    public void unregister() {
        if (entity != null) entity.unregister();
        if (notification != null) Events.RENDER_HUD.unregister(this);
    }

    public boolean entityExists() {
        return entity == null || entity.hasMatch();
    }

    public boolean dwarvenEventActive() {
        return dwarvenEvent == null || DwarvenEvents.isActive(dwarvenEvent);
    }

    public boolean areMet() {
        boolean met = entityExists() && dwarvenEventActive();

        if (isNotifying) return met;
        
        if (met && !wasMet && offCooldown()) {
            isNotifying = true;
            sendNotification();
            isNotifying = false;
            lastNotified = System.currentTimeMillis();
        }
        
        wasMet = met;
        return met;
    }

    private boolean offCooldown() {
        return (System.currentTimeMillis() - lastNotified) > COOLDOWN_MS;
    }

    private void sendNotification() {
        if (notification == null || notification.isEmpty() || CLIENT.player == null) return;

        CLIENT.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        Guide.lerping = true;
        String instructions = ActiveSteps.getCombinedInstructions();
        display = new Display(instructions).setScale(4);

        this.activeTitle = new AnimatedTitle(
            display,
            display.titleX(),
            display.titleY(),
            10,
            12,
            NOTIFICATION_SCALE,
            1,
            1000,
            1000
        );
    }

    @Override
    public void onRenderHud(GuiGraphicsExtractor graphics, DeltaTracker tickDelta) {
        if (activeTitle != null) {
            activeTitle.render(graphics);
            
            if (activeTitle.isFinished()) {
                activeTitle = null;
                Guide.lerping = false;
            }
        }
    }
}