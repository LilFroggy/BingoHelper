package io.github.lilfroggy.bingohelper.guide.step;

import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.guide.step.properties.async.AsyncProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.bingoRanks.BingoRanksProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.highlightSlots.HighlightSlotsProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.navTo.NavToProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.outlineEntities.OutlineEntitiesProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.waypoint.WaypointProperty;

import java.util.List;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.GlowingEntities;
import net.minecraft.client.MinecraftClient;

public abstract class Step {
    protected static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    protected abstract String formattedInstruction();
    protected abstract void onInit();
    protected abstract void onReset();
    protected abstract void onActivate();
    protected abstract void onDeactivate();
    private boolean isActive;
    private int index;

    public String type;
    public String instruction;
    public String command;
    public NavToProperty navTo;
    public WaypointProperty waypoint;
    public List<OutlineEntitiesProperty> outlineEntities;
    public List<HighlightSlotsProperty> highlightSlots;
    public BingoRanksProperty bingoRanks;
    public AsyncProperty async;

    public void init() {
        onInit();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isAsync() {
        return async != null;
    }

    public boolean isBlocking() {
        return isAsync() ? async.isBlocking() : true;
    }

    public int registrationIndex() {
        return this.index;
    }

    public int effectiveIndex() {
        return isAsync() ? async.effectiveIndex() : registrationIndex();
    }

    public boolean isHidden() {
        return isAsync() ? async.isHidden() : false;
    }

    public final String instruction() {
        String base = this.instruction != null ? this.instruction : "";

        String formatted = formattedInstruction();
        if (formatted == null) formatted = base;

        return formatted.replaceAll("%visitIsland%", Config.visitIsland);
    }

    public final void reset() {
        if (waypoint != null) waypoint.reset();

        onReset();
    }

    public final void complete() {
        Guide.advance(this);
    }

    public final void activate() {
        if (!Config.guide) return;
        if (!Skyblock.inBingo()) return;
        if (isActive()) return;
        isActive = true;

        if (navTo != null) navTo.register();
        if (outlineEntities != null) outlineEntities.forEach(p -> p.register());
        if (highlightSlots != null) highlightSlots.forEach(p -> p.register());
        if (waypoint != null) waypoint.register(outlineEntities);
        if (bingoRanks != null) bingoRanks.register(this);
        if (async != null) async.register();

        Guide.stepStartTime = System.currentTimeMillis();

        onActivate();
        if (Config.debug) Logger.info("Activated: " + this.getClass().getSimpleName() + this.hashCode());
    }

    public final void deactivate() {
        if (!isActive()) return;
        isActive = false;

        if (navTo != null) navTo.unregister();
        if (outlineEntities != null) outlineEntities.forEach(OutlineEntitiesProperty::unregister);
        if (highlightSlots != null) highlightSlots.forEach(HighlightSlotsProperty::unregister);
        if (waypoint != null) waypoint.unregister();
        if (bingoRanks != null) bingoRanks.unregister();
        if (async != null) async.unregister();
        GlowingEntities.clear();

        onDeactivate();
        if (Config.debug) Logger.info("Deactivated: " + this.getClass().getSimpleName() + this.hashCode());
    }
}