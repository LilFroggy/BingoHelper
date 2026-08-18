package io.github.lilfroggy.bingohelper.guide.step.properties.async;

import java.util.ArrayList;
import java.util.List;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderHudEvent;
import io.github.lilfroggy.bingohelper.guide.ActiveSteps;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.guide.step.properties.async.requirements.DwarvenEventRequirement;
import io.github.lilfroggy.bingohelper.guide.step.properties.async.requirements.EntityRequirement;
import io.github.lilfroggy.bingohelper.guide.step.properties.async.requirements.Requirement;
import io.github.lilfroggy.bingohelper.guide.step.properties.async.requirements.SkyblockLevelRequirement;
import io.github.lilfroggy.bingohelper.guide.step.properties.async.requirements.WaitSecondsRequirement;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.entity.EntityPredicate;
import io.github.lilfroggy.bingohelper.util.render.AnimatedTitle;
import io.github.lilfroggy.bingohelper.util.render.Display;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;

public class AsyncRequirements implements RenderHudEvent {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private final List<Requirement> requirements = new ArrayList<>();

    public Step step;
    private AnimatedTitle activeTitle;
    private Display display;
    private final float NOTIFICATION_SCALE = 4;
    private boolean isNotifying = false;
    private boolean areMet = false;

    public EntityPredicate entity;
    public String dwarvenEvent;
    public Integer skyblockLevel;
    public Integer waitSeconds;

    private long lastNotified = 0;
    private static final long COOLDOWN_MS = 10000;

    public void register(Step step) {
        this.areMet = false;
        this.step = step;

        if (entity != null) requirements.add(new EntityRequirement(entity));
        if (dwarvenEvent != null) requirements.add(new DwarvenEventRequirement(dwarvenEvent));
        if (skyblockLevel != null) requirements.add(new SkyblockLevelRequirement(skyblockLevel));
        if (waitSeconds != null) requirements.add(new WaitSecondsRequirement(waitSeconds));

        requirements.forEach(requirement -> requirement.register(this::check));

        check(); // Initial check
    }

    public void unregister() {
        requirements.forEach(requirement -> requirement.unregister());
        requirements.clear();
    }

    public boolean check() {
        ChatLib.chat("checking async requirements");

        boolean wasMet = areMet;
        
        areMet = requirements.stream().allMatch(requirement -> requirement.isMet());

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

    public void onStateChange(boolean meetsRequirements) {
        ActiveSteps.setPriority(step, meetsRequirements);
        if (meetsRequirements) step.registerListeners();
        else step.unregisterListeners();
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
    public String toString() {
        return "AsyncRequirements{" +
                "areMet=" + areMet +
                ", requirements=" + requirements +
                '}';
    }
}