package io.github.lilfroggy.bingohelper.guide.step.properties.async;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderHudEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.SkyblockLevelChangeEvent;
import io.github.lilfroggy.bingohelper.guide.ActiveSteps;
import io.github.lilfroggy.bingohelper.guide.step.Step;
//import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.DwarvenEvents;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.interfaces.DwarvenEventEndEvent;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.interfaces.DwarvenEventStartEvent;
import io.github.lilfroggy.bingohelper.util.entity.EntityPredicate;
import io.github.lilfroggy.bingohelper.util.render.AnimatedTitle;
import io.github.lilfroggy.bingohelper.util.render.Display;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;

public class AsyncRequirements implements DwarvenEventStartEvent, DwarvenEventEndEvent, SkyblockLevelChangeEvent, RenderHudEvent {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public Step step;
    private AnimatedTitle activeTitle;
    private Display display;
    private final float NOTIFICATION_SCALE = 4;
    private boolean isNotifying = false;
    private boolean areMet = false;

    public EntityPredicate entity;
    public String dwarvenEvent;
    public Integer skyblockLevel;

    private long lastNotified = 0;
    private static final long COOLDOWN_MS = 10000;

    public void register(Step step) {
        this.areMet = false;
        this.step = step;
        if (entity != null) entity.register(hasMatch -> check());
        if (dwarvenEvent != null) DwarvenEvents.ON_START.register(this);
        if (dwarvenEvent != null) DwarvenEvents.ON_END.register(this);
        if (skyblockLevel != null) Events.SKYBLOCK_LEVEL_CHANGE.register(this);
        check(); // Initial check
    }

    public void unregister() {
        if (entity != null) entity.unregister();
        if (dwarvenEvent != null) DwarvenEvents.ON_START.unregister(this);
        if (dwarvenEvent != null) DwarvenEvents.ON_END.unregister(this);
        if (skyblockLevel != null) Events.SKYBLOCK_LEVEL_CHANGE.unregister(this);
    }

    public boolean check() {
        boolean wasMet = areMet;
        
        areMet = entityExists() && dwarvenEventActive() && isSkyblockLevel();

        //Logger.info(step.globallyFormatted() + ": " + areMet);
        
        if (areMet != wasMet) onStateChange(areMet);

        if (isNotifying) return areMet;
        
        if (areMet && !wasMet && offCooldown()) {
            isNotifying = true;
            sendNotification();
            isNotifying = false;
            lastNotified = System.currentTimeMillis();
        }

        return areMet;
    }

    public void onStateChange(boolean isMet) {
        ActiveSteps.setPriority(step, isMet);
        if (isMet) step.registerListeners();
        else step.unregisterListeners();
    }

    public boolean entityExists() {
        return entity == null || entity.hasMatch();
    }

    public boolean dwarvenEventActive() {
        return dwarvenEvent == null || DwarvenEvents.isActive(dwarvenEvent);
    }

    public boolean isSkyblockLevel() {
        return skyblockLevel == null || Skyblock.level() >= skyblockLevel;
    }

    public boolean areMet() {
        return areMet;
    }

    private boolean offCooldown() {
        return (System.currentTimeMillis() - lastNotified) > COOLDOWN_MS;
    }

    private void sendNotification() {
        if (CLIENT.player == null) return;

        CLIENT.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        String instruction = step.instruction();
        Display guide = ActiveSteps.display;
        display = new Display(instruction).setScale(NOTIFICATION_SCALE);

        double finalY = Config.instructionsOnly ? guide.y() : guide.y() + guide.lineHeight();

        this.activeTitle = new AnimatedTitle(
            display,
            display.titleX(),
            display.titleY(),
            guide.x(),
            finalY,
            NOTIFICATION_SCALE,
            guide.scale(),
            1000,
            1000
        );

        Events.RENDER_HUD.register(this);
    }

    @Override
    public void onRenderHud(GuiGraphicsExtractor graphics, DeltaTracker tickDelta) {
        activeTitle.render(graphics);
        if (!activeTitle.isFinished()) return;
        Events.RENDER_HUD.unregister(this);
        activeTitle = null;
    }

    @Override
    public void onDwarvenEventStart(String event) {
        if (event.equals(dwarvenEvent)) check();
    }

    @Override
    public void onDwarvenEventEnd(String event) {
        if (event.equals(dwarvenEvent)) check();
    }

    @Override
    public void onSkyblockLevelChange(int level) {
        check();
    }
}