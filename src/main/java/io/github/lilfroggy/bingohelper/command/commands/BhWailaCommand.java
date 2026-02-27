package io.github.lilfroggy.bingohelper.command.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
import io.github.lilfroggy.bingohelper.util.JsonUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BhWailaCommand implements ClientCommand {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("bhwaila")
            .executes(this::executeNbt)
            .then(ClientCommandManager.literal("waypoint")
                .executes(ctx -> executeFormatted(ctx, true, false)))
            .then(ClientCommandManager.literal("outline")
                .executes(ctx -> executeFormatted(ctx, false, true)))
            .then(ClientCommandManager.literal("outlinewaypoint")
                .executes(ctx -> executeFormatted(ctx, true, true)))
        );
    }

    private int executeNbt(CommandContext<FabricClientCommandSource> context) {
        HitResult hitResult = CLIENT.crosshairTarget;
        if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
            ChatLib.chat("§cNot looking at an entity!");
            return 0;
        }

        Entity entity = ((EntityHitResult) hitResult).getEntity();
        NbtWriteView view = NbtWriteView.create(ErrorReporter.EMPTY);
        entity.writeData(view);

        Text nbt = NbtHelper.toPrettyPrintedText(view.getNbt());
        ClipboardUtils.setClipboard(nbt.getString());
        ChatLib.chatNoPrefix(nbt);
        return 1;
    }

    private int executeFormatted(CommandContext<FabricClientCommandSource> context, boolean includeWaypoint, boolean includeOutline) {
        HitResult hitResult = CLIENT.crosshairTarget;
    
        if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
            ChatLib.chat("§cNot looking at anything!");
            return 0;
        }
    
        double x, y, z;
        String entityType = "unknown";
        String hitType = "";
        String targetLabel = "unknown";
        String dataFormat = "unknown";
    
        if (hitResult instanceof EntityHitResult entityHit) {
            Entity entity = entityHit.getEntity();
            Vec3d pos = entity.getEntityPos();
            x = pos.x; y = pos.y; z = pos.z;
            entityType = entity.getType().getName().getString();
            hitType = " entity";
            targetLabel = entity.getDisplayName().getString();
        } else if (hitResult instanceof BlockHitResult blockHit && !includeOutline) {
            BlockPos pos = blockHit.getBlockPos();
            x = pos.getX(); y = pos.getY() + 1; z = pos.getZ();
            targetLabel = CLIENT.world.getBlockState(pos).getBlock().getName().getString();
        } else {
            ChatLib.chat("§cYou must be looking at an entity for outlines!");
            return 0;
        }

        JsonObject root = new JsonObject();

        if (includeWaypoint) {
            JsonObject waypoint = new JsonObject();
            waypoint.addProperty("text", "");
            
            JsonArray posArray = new JsonArray();
            posArray.add(x);
            posArray.add(y);
            posArray.add(z);
            waypoint.add("position", posArray);
            waypoint.addProperty("radius", 0);

            if (includeOutline) {
                dataFormat = "outline waypoint";
                JsonObject waypointListWrapper = new JsonObject();
                waypointListWrapper.addProperty("type", "list");
                waypointListWrapper.addProperty("beam", true);
                
                JsonArray list = new JsonArray();
                list.add(waypoint);
                
                waypointListWrapper.add("list", list);
                waypointListWrapper.addProperty("index", 0);
                root.add("waypoint", waypointListWrapper);
            } else {
                dataFormat = "waypoint";
                root = waypoint; // If ONLY waypoint, the root is the waypoint object itself
            }
        }
    
        if (includeOutline) {
            if (!includeWaypoint) dataFormat = "outline";
            JsonObject outline = new JsonObject();
            outline.addProperty("entityType", entityType);
            
            JsonArray posArray = new JsonArray();
            posArray.add(x);
            posArray.add(y);
            posArray.add(z);
            outline.add("position", posArray);
            
            root.add("outlineEntity", outline);
        }
    
        String json = JsonUtils.toPretty(root);

        ClipboardUtils.setClipboard(json);
        ChatLib.chat("§aCopied §b" + targetLabel + "§a" + hitType + " " + dataFormat + " data to clipboard!");
        return 1;
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