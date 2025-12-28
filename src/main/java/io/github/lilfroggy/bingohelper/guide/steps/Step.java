package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.WorldRenderEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.EntityRenderEventBus;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.Outline;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public abstract class Step implements
        EntityRenderEventBus.EntityRenderListener,
        WorldRenderEventBus.WorldRenderListener {

    public abstract String additionalInstructionFormatting();
    protected abstract void onReset();
    protected abstract void onActivate();
    protected abstract void onDeactivate();
    public Boolean isActive = false;
    public Boolean outlineEntityExists = false;

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
            public String text;
            public List<Double> position;
            public int radius;
        }
    }

    public static class OutlineEntity {
        public String entityType;
        public List<Double> position;
    }

    public final String formattedInstruction() {
        String formatted = "" + additionalInstructionFormatting();
        return formatted.replaceAll("%visitIsland%", Config.visitIsland);
    };

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

        if (outlineEntity != null) EntityRenderEventBus.register(this);
        if (waypoint != null) WorldRenderEventBus.register(this);

        Guide.stepStartTime = System.currentTimeMillis();

        // Call subclass logic
        onActivate();
        if (Config.debug) Logger.info("Activated: " + this.getClass().getSimpleName() + this.hashCode());
    }

    public final void deactivate() {
        if (!isActive) return;
        isActive = false;

        EntityRenderEventBus.unregister(this);
        WorldRenderEventBus.unregister(this);

        Outline.clearEntities();

        // Call subclass logic
        onDeactivate();
        if (Config.debug) Logger.info("Deactivated: " + this.getClass().getSimpleName() + this.hashCode());
    }

    @Override
    public void onEntityRender(Entity entity, double cameraX, double cameraY, double cameraZ,
                               float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        OutlineEntity e = outlineEntity;
        if(e == null) return;
        if(!entity.getType().getName().getString().equals(e.entityType)) return;
        Vec3d pos = new Vec3d(e.position.get(0), e.position.get(1), e.position.get(2));
        if(!entity.getPos().equals(pos)) return;
        outlineEntityExists = true;
        Outline.outlineEntity(entity, vertexConsumers, 0, 255, 255, 255);
    }

    @Override
    public void onWorldRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, WorldRenderContext context) {
        if (waypoint == null || waypoint.list == null || waypoint.list.isEmpty() || waypoint.index >= waypoint.list.size()) return;
        
        // Get current waypoint
        Waypoint.WaypointEntry currentWaypoint = waypoint.list.get(waypoint.index);
        
        // Get player position
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        
        Vec3d playerPos = mc.player.getPos();
        Vec3d waypointPos = new Vec3d(currentWaypoint.position.get(0) + 0.5, currentWaypoint.position.get(1) + 1.5, currentWaypoint.position.get(2) + 0.5);
        Vec3d linePos = new Vec3d(currentWaypoint.position.get(0) + 0.5, currentWaypoint.position.get(1) + 0.5, currentWaypoint.position.get(2) + 0.5);
        
        // Calculate distance to waypoint
        double distance = playerPos.distanceTo(waypointPos);
        
        // Check if player is within radius
        if (distance <= currentWaypoint.radius) {
            // Player is within radius, advance to next waypoint
            if (waypoint.index < waypoint.list.size() - 1) {
                waypoint.index++;
            }
        } else {
            // Player is outside radius, draw line to waypoint
            if (outlineEntity == null || waypoint.index < waypoint.list.size() - 1) {
                double x = currentWaypoint.position.get(0);
                double y = currentWaypoint.position.get(1);
                double z = currentWaypoint.position.get(2);
                float[] fillColor = {0.0f, 1.0f, 1.0f};
                RenderLib.renderFilled(context, x, y, z, x + 1, y + 1, z + 1, fillColor, 0.5f, false);
                RenderLib.renderOutline(context, x, y, z, x + 1, y + 1, z + 1, fillColor, 1.0f, 5.0f, true);
            }

            float[] colorComponents = {0.0f, 1.0f, 1.0f};
            float alpha = 1.0f;
            float lineWidth = 2.0f;
            
            RenderLib.renderLineFromCursor(context, linePos, colorComponents, alpha, lineWidth);
            
            // Render waypoint text only when outside radius and text is not null/empty
            if (!outlineEntityExists && currentWaypoint.text != null && !currentWaypoint.text.trim().isEmpty()) {
                Text waypointText = Text.literal(currentWaypoint.text);
                RenderLib.renderText(context, waypointText.asOrderedText(), waypointPos, 0.2f, 0.0f, true);
            }
        }
        outlineEntityExists = false;
    }

}