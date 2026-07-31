package io.github.lilfroggy.bingohelper.guide.step;

import io.github.lilfroggy.bingohelper.guide.ActiveSteps;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.guide.step.properties.async.AsyncProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.bingoRanks.BingoRanksProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.highlightSlots.HighlightSlotsProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.navTo.NavToProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.outlineEntities.OutlineEntitiesProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.prerequisites.PrerequisitesProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.waypoint.WaypointProperty;
import io.github.lilfroggy.bingohelper.messages.Messages;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.JsonDataObject;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Scheduler;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.GlowingEntities;

public abstract class Step {
    protected static final Minecraft CLIENT = Minecraft.getInstance();

    protected abstract String locallyFormatted();
    protected abstract void onInit();
    protected abstract void onReset();
    protected abstract void onActivate();
    protected abstract void onDeactivate();

    private boolean isActive;
    private boolean isComplete;
    public int index;
    public Step parent;
    public long startTimeMs;
    public long lastProgressMs;
    public boolean isPriority;

    public String type;
    public String instruction = "";
    public String command;
    public NavToProperty navTo;
    public WaypointProperty waypoint;
    public List<OutlineEntitiesProperty> outlineEntities;
    public List<HighlightSlotsProperty> highlightSlots;
    public BingoRanksProperty bingoRanks;
    public AsyncProperty async;
    public PrerequisitesProperty prerequisites;

    public void init() {
        onInit();
    }

    public boolean isActive() {
        return isActive;
    }

    public long startTimeMs() {
        return startTimeMs;
    }

    public long lastProgressMs() {
        return lastProgressMs;
    }

    public void onProgress() {
        lastProgressMs = System.currentTimeMillis();
        ActiveSteps.dirty = true;
    }

    public boolean isAsync() {
        return async != null;
    }

    public boolean isBlocking() {
        return isAsync() ? async.isBlocking() : true;
    }

    public int registrationIndex() {
        return index;
    }

    public int effectiveAt() {
        return isAsync() ? async.effectiveAt() : registrationIndex();
    }

    public boolean isHidden() {
        return isAsync() ? async.isHidden() : false;
    }

    public boolean hasRequirements() {
        return async != null && async.hasRequirements();
    }

    public boolean meetsRequirements() {
        return async == null || async.meetsRequirements();
    }

    public boolean isPriority() {
        return isPriority;
    }

    public boolean isPrerequisite() {
        return parent != null && parent.hasPrerequisites();
    }

    public boolean hasPrerequisites() {
        return prerequisites != null;
    }

    public final String instruction() {
        return hasPrerequisites() ? prerequisites.instruction() : globallyFormatted();
    }

    @Nullable
    public final String command() {
        return hasPrerequisites() ? prerequisites.command() : command;
    }

    public final String globallyFormatted() {
        String formatted = locallyFormatted()
            .replaceAll("%visitIsland%", Config.visitIsland);

        return isPriority ? ChatLib.toBold(formatted) : formatted;
    }

    public final void reset() {
        if (waypoint != null) waypoint.reset();
        if (prerequisites != null) prerequisites.reset();
        onReset();
    }

    public final void previousPrerequisite() {
        parent.prerequisites.previous();
    }

    public final void nextPrerequisite() {
        parent.prerequisites.next();
    }

    public final void complete() {
        if (isComplete) return;
        
        isComplete = true;

        ActiveSteps.remove(this);

        long currentTimeMs = System.currentTimeMillis();
        long elapsedMs = currentTimeMs - startTimeMs;
        long elapsedSeconds = elapsedMs / 1000;

        String message = Messages.STEP_COMPLETE.formatted(
            ChatLib.replaceAmpersands(globallyFormatted().replaceAll("\n", " ")), 
            ChatLib.formatDuration(elapsedSeconds)
        );

        Scheduler.SCHEDULER.schedule(() -> {
            CLIENT.execute(() -> {
                ChatLib.chat(message);
            });
        }, 250, TimeUnit.MILLISECONDS);

        if (isPrerequisite()) nextPrerequisite();
        else Guide.advance();
    }

    public final void registerListeners() {
        if (prerequisites != null) prerequisites.register(this);
        if (navTo != null) navTo.register(outlineEntities);
        if (outlineEntities != null) outlineEntities.forEach(p -> p.register());
        if (highlightSlots != null) highlightSlots.forEach(p -> p.register());
        if (waypoint != null) waypoint.register(outlineEntities);
        if (bingoRanks != null) bingoRanks.register(this);

        onActivate();
    }

    public final void unregisterListeners() {
        if (navTo != null) navTo.unregister();
        if (outlineEntities != null) outlineEntities.forEach(OutlineEntitiesProperty::unregister);
        if (highlightSlots != null) highlightSlots.forEach(HighlightSlotsProperty::unregister);
        if (waypoint != null) waypoint.unregister();
        if (bingoRanks != null) bingoRanks.unregister();
        if (prerequisites != null) prerequisites.unregister();
        GlowingEntities.clear();

        onDeactivate();
    }

    public final void activate() {
        if (!Config.guide) return;
        if (!Skyblock.inBingo()) return;
        if (isActive) return;

        isActive = true;
        isComplete = false;
        startTimeMs = System.currentTimeMillis();

        if (async != null) async.register(this);

        if (!hasRequirements()) registerListeners();

        onActivate();
        Logger.debug("Activated: " + this.getClass().getSimpleName() + this.hashCode());
    }

    public final void deactivate() {
        if (!isActive) return;

        isActive = false;

        if (async != null) async.unregister();
        
        unregisterListeners();

        onDeactivate();
        Logger.debug("Deactivated: " + this.getClass().getSimpleName() + this.hashCode());
    }

    public final JsonDataObject state() {
        JsonDataObject state = new JsonDataObject();
        state.set("index", registrationIndex());

        if (hasPrerequisites()) {
            state.set("prerequisites$index", prerequisites.index);
        }
        
        return state;
    }
}