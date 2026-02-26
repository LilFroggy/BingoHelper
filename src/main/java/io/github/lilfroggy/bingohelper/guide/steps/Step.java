package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.WorldRenderEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.EntityStateUpdateEventBus;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.GlowingEntities;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public abstract class Step implements
        EntityStateUpdateEventBus.EntityStateUpdateListener,
        WorldRenderEventBus.WorldRenderListener {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final float[] WAYPOINT_COLOR = {0.0f, 1.0f, 1.0f};

    public abstract String additionalInstructionFormatting();
    protected abstract void onReset();
    protected abstract void onActivate();
    protected abstract void onDeactivate();
    public Boolean isActive = false;

    public String type;
    public String instruction;
    public String command;
    public String clientCommand;
    public Waypoint waypoint;
    public OutlineEntity outlineEntity;

    public static class Waypoint {
        public List<WaypointEntry> list;
        public int index;

        public static class WaypointEntry {
            // Provided
            public String text;
            public List<Double> position;
            public int radius;

            // Internal
            public transient Vec3d cachedPos;

            public Vec3d getPos() {
                if (cachedPos == null) cachedPos = new Vec3d(
                    position.get(0),
                    position.get(1),
                    position.get(2)
                );
                return cachedPos;
            }
        }
    }

    public static class OutlineEntity {
        // Provided
        public String entityType;
        public List<Double> position;

        // Internal
        public transient Entity mcEntity;
        public transient Vec3d cachedPos;
        public transient boolean exists;

        public Vec3d getPos() {
            if (cachedPos == null) cachedPos = new Vec3d(
                position.get(0),
                position.get(1),
                position.get(2)
            );
            return cachedPos;
        }
    }

    public final String formattedInstruction() {
        String formatted = "" + additionalInstructionFormatting();
        return formatted.replaceAll("%visitIsland%", Config.visitIsland);
    }

    public final void reset() {
        if (waypoint != null) waypoint.index = 0;

        // Call subclass logic
        onReset();
    }

    public final void activate() {
        if (!Config.guide) return;
        if (!Skyblock.inBingo()) return;
        if (isActive) return;
        isActive = true;

        if (outlineEntity != null) EntityStateUpdateEventBus.register(this);
        if (waypoint != null) WorldRenderEventBus.register(this);

        Guide.stepStartTime = System.currentTimeMillis();

        // Call subclass logic
        onActivate();
        if (Config.debug) Logger.info("Activated: " + this.getClass().getSimpleName() + this.hashCode());
    }

    public final void deactivate() {
        if (!isActive) return;
        isActive = false;

        EntityStateUpdateEventBus.unregister(this);
        WorldRenderEventBus.unregister(this);
        GlowingEntities.clear();

        // Call subclass logic
        onDeactivate();
        if (Config.debug) Logger.info("Deactivated: " + this.getClass().getSimpleName() + this.hashCode());
    }

    @Override
    public void onUpdateEntityState(Entity entity, EntityRenderState state) {
        if (waypoint != null && waypoint.index != waypoint.list.size() - 1) return;
        if (outlineEntity == null) return;

        boolean typeMatches = entity.getType().getName().getString().equals(outlineEntity.entityType);
        boolean posMatches = entity.getEntityPos().equals(outlineEntity.getPos());

        if (!typeMatches || !posMatches) return;

        outlineEntity.mcEntity = entity;
        outlineEntity.exists = true;
        GlowingEntities.add(entity, state, 0, 255, 255, 255);
    }

    @Override
    public void onWorldRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, WorldRenderContext context) {
        try {
            if (waypoint == null) return;
            
            boolean onLastWaypoint = waypoint.index == waypoint.list.size() - 1;

            if (onLastWaypoint && outlineEntity != null && outlineEntity.exists && outlineEntity.mcEntity != null) {
                Vec3d entityMid = outlineEntity.mcEntity.getEntityPos().add(0, outlineEntity.mcEntity.getHeight() / 2, 0);
                RenderLib.renderLineFromCursor(context, entityMid, WAYPOINT_COLOR, 1.0f, 3.0f);
                return;
            }

            if (CLIENT.player == null) return;

            Waypoint.WaypointEntry wp = waypoint.list.get(waypoint.index);
            Vec3d wpPos = wp.getPos();
            Vec3d targetCenter = wpPos.add(0.5, 1.5, 0.5);
            double distance = CLIENT.player.getEntityPos().distanceTo(targetCenter);

            if (distance <= wp.radius) {
                if (waypoint.index < waypoint.list.size() - 1) {
                    waypoint.index++;
                }
                return;
            }

            if (outlineEntity == null || !onLastWaypoint) {
                double x = wpPos.x, y = wpPos.y, z = wpPos.z;
                RenderLib.renderFilled(context, x, y, z, x + 1, y + 1, z + 1, WAYPOINT_COLOR, 0.5f, false);
                RenderLib.renderOutline(context, x, y, z, x + 1, y + 1, z + 1, WAYPOINT_COLOR, 1.0f, 5.0f, true);
            }

            RenderLib.renderLineFromCursor(context, wpPos.add(0.5, 0.5, 0.5), WAYPOINT_COLOR, 1.0f, 3.0f);

            if (wp.text != null) {
                RenderLib.renderText(Text.literal(wp.text).asOrderedText(), wpPos.add(0.5, 0.5, 0.5), 0.2f, 0.0f, true);
            }
        } catch (Exception e) {
            Logger.error("Error rendering waypoint", e);
        } finally {
            if (outlineEntity != null) outlineEntity.exists = false;
        }
    }
}