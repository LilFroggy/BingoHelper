package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
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
            x = pos.getX(); y = pos.getY(); z = pos.getZ();
            targetLabel = CLIENT.world.getBlockState(pos).getBlock().getName().getString();
        } else {
            ChatLib.chat("§cYou must be looking at an entity for outlines!");
            return 0;
        }
    
        StringBuilder json = new StringBuilder();
    
        if (includeWaypoint) {
            if (includeOutline) {
                dataFormat = "outline waypoint";
                json.append(String.format(
                    "\"waypoint\": {\n" +
                    "    \"type\": \"list\",\n" +
                    "    \"beam\": true,\n" +
                    "    \"list\": [\n" +
                    "        {\n" +
                    "            \"text\": \"\",\n" +
                    "            \"position\": [\n" +
                    "                %.1f,\n" +
                    "                %.1f,\n" +
                    "                %.1f\n" +
                    "            ],\n" +
                    "            \"radius\": 0\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"index\": 0\n" +
                    "}", x, y, z));

                json.append(",\n");
            } else {
                dataFormat = "waypoint";
                json.append(String.format(
                    "{\n" +
                    "    \"text\": \"\",\n" +
                    "    \"position\": [\n" +
                    "        %.1f,\n" +
                    "        %.1f,\n" +
                    "        %.1f\n" +
                    "    ],\n" +
                    "    \"radius\": 0\n" +
                    "}", x, y, z));
            }
        }
    
        if (includeOutline) {
            if (!includeWaypoint) dataFormat = "outline";
            json.append(String.format(
                "\"outlineEntity\": {\n" +
                "    \"entityType\": \"%s\",\n" +
                "    \"position\": [\n" +
                "        %.1f,\n" +
                "        %.1f,\n" +
                "        %.1f\n" +
                "    ]\n" +
                "}", entityType, x, y, z));
        }
    
        ClipboardUtils.setClipboard(json.toString());
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