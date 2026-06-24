package io.github.lilfroggy.bingohelper.command.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
import io.github.lilfroggy.bingohelper.util.EntityUtils;
import io.github.lilfroggy.bingohelper.util.JsonUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BhWailaCommand implements ClientCommand {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bhwaila")
            .executes(this::executeNbt)
            .then(ClientCommands.literal("waypoint").executes(this::executeWaypoint))
            .then(ClientCommands.literal("waypointFull").executes(this::executeWaypointFull))
            .then(ClientCommands.literal("outline").executes(this::executeOutline))
            .then(ClientCommands.literal("outlineFull").executes(this::executeOutlineFull))
            .then(ClientCommands.literal("outlinewaypoint").executes(this::executeOutlineWaypoint))
        );
    }

    private int executeNbt(CommandContext<FabricClientCommandSource> ctx) {
        Entity target = getTargetEntity();
        if (target == null) return 0;

        TagValueOutput view = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        target.saveWithoutId(view);
        Component nbt = NbtUtils.toPrettyComponent(view.buildResult());
        
        copyAndNotify(nbt.getString(), "NBT data");
        return 1;
    }

    private int executeWaypoint(CommandContext<FabricClientCommandSource> ctx) {
        HitResult hit = CLIENT.hitResult;
        if (hit == null || hit.getType() == HitResult.Type.MISS) return sendError("Not looking at anything!");

        JsonObject waypoint = createWaypointJson(hit);
        String json = JsonUtils.toPretty(waypoint);

        copyAndNotify(json, "waypoint data");
        return 1;
    }

    private int executeWaypointFull(CommandContext<FabricClientCommandSource> ctx) {
        HitResult hit = CLIENT.hitResult;
        if (hit == null || hit.getType() == HitResult.Type.MISS) return sendError("Not looking at anything!");

        JsonObject waypointFull = createWaypointFullJson(hit);
        String json = removeRootBrackets(JsonUtils.toPretty(waypointFull));

        copyAndNotify(json, "full waypoint data");
        return 1;
    }

    private int executeOutline(CommandContext<FabricClientCommandSource> ctx) {
        Entity target = getTargetEntity();
        if (target == null) return 0;

        JsonObject outline = createOutlineJson(target);
        String json = JsonUtils.toPretty(outline);

        copyAndNotify(json, "outline data");
        return 1;
    }

    private int executeOutlineFull(CommandContext<FabricClientCommandSource> ctx) {
        Entity target = getTargetEntity();
        if (target == null) return 0;

        JsonObject outlineFull = createOutlineFullJson(target);
        String json = removeRootBrackets(JsonUtils.toPretty(outlineFull));

        copyAndNotify(json, "full outline data");
        return 1;
    }

    private int executeOutlineWaypoint(CommandContext<FabricClientCommandSource> ctx) {
        Entity target = getTargetEntity();
        if (target == null) return sendError("You must be looking at an entity for outlines!");

        JsonObject root = new JsonObject();
        root.add("waypoint", createWaypointFullJson(CLIENT.hitResult).get("waypoint"));
        root.add("outlineEntities", createOutlineFullJson(target).get("outlineEntities"));
        String json = removeRootBrackets(JsonUtils.toPretty(root));

        copyAndNotify(json, "full outline waypoint data");
        return 1;
    }

    private Entity getTargetEntity() {
        if (CLIENT.hitResult instanceof EntityHitResult res) return res.getEntity();
        sendError("Not looking at an entity!");
        return null;
    }

    private JsonObject createWaypointJson(HitResult hit) {
        double x, y, z;

        if (hit instanceof EntityHitResult entityHit) {
            Entity entity = entityHit.getEntity();
            x = entity.getX() - 0.5;
            y = entity.getY() + (entity.getBbHeight() / 2) - 0.5;
            z = entity.getZ() - 0.5;
        } else if (hit instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            x = pos.getX();
            y = pos.getY() + 1;
            z = pos.getZ();
        } else {
            Vec3 pos = hit.getLocation();
            x = pos.x; y = pos.y; z = pos.z;
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("text", "");
        JsonArray arr = new JsonArray();
        arr.add(x); arr.add(y); arr.add(z);
        obj.add("position", arr);
        obj.addProperty("radius", 0);
        return obj;
    }

    private JsonObject createWaypointFullJson(HitResult hit) {
        JsonObject root = new JsonObject();
        JsonObject obj = new JsonObject();
        root.add("waypoint", obj);
        obj.addProperty("type", "list");
        obj.addProperty("beam", true);
        JsonArray list = new JsonArray();
        list.add(createWaypointJson(hit));
        obj.add("list", list);
        obj.addProperty("index", 0);
        return root;
    }

    private JsonObject createOutlineJson(Entity entity) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", entity.getType().getDescription().getString());
        JsonArray pos = new JsonArray();
        pos.add(entity.getX()); pos.add(entity.getY()); pos.add(entity.getZ());
        obj.add("position", pos);
        if (entity instanceof AbstractClientPlayer player) {
            String skin = EntityUtils.getPlayerSkin(player);
            obj.add("skin", new JsonPrimitive(skin));
        }
        return obj;
    }

    private JsonObject createOutlineFullJson(Entity entity) {
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();
        root.add("outlineEntities", arr);
        arr.add(createOutlineJson(entity));
        return root;
    }

    private String removeRootBrackets(String obj) {
        return obj.substring(1, obj.length() - 1).trim();
    }

    private void copyAndNotify(String data, String type) {
        ClipboardUtils.setClipboard(data);
        ChatLib.chat("§aCopied §b" + type + "§a to clipboard!");
    }

    private int sendError(String message) {
        ChatLib.chat("§c" + message);
        return 0;
    }

    @Override
    public String getName() {
        return "bhwaila";
    }

    @Override
    public String getDescription() {
        return "Copies data of the target to clipboard (NBT, Waypoint, or Outline)";
    }
}