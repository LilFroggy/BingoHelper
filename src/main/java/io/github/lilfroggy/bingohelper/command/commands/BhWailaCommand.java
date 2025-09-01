package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class BhWailaCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("bhwaila")
                .executes(this::execute));
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.world == null) {
            context.getSource().sendFeedback(Text.literal("§cNot in a world!"));
            return 0;
        }

        // Get what the player is looking at
        HitResult hitResult = client.crosshairTarget;

        if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
            context.getSource().sendFeedback(Text.literal("§cNot looking at an entity!"));
            return 0;
        }

        EntityHitResult entityHitResult = (EntityHitResult) hitResult;
        Entity entity = entityHitResult.getEntity();

        String nbtString = "";
        boolean clipboardSuccess = false;

        try {
            // Get the raw NBT data from the entity
            NbtCompound rawNbt = new NbtCompound();
            entity.writeNbt(rawNbt);

            Text nbt = NbtHelper.toPrettyPrintedText(rawNbt);

            nbtString = nbt.getString();

            ChatLib.chat(nbt);

        } catch (Exception ignored) {}

        // Method 1: Try AWT Toolkit
        try {
            StringSelection stringSelection = new StringSelection(nbtString);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            clipboardSuccess = true;
        } catch (Exception e) {
            // AWT failed, try other methods
        }

        // Method 2: Try system clipboard command (Linux/Mac)
        if (!clipboardSuccess) {
            try {
                ProcessBuilder pb;
                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("linux")) {
                    pb = new ProcessBuilder("xclip", "-selection", "clipboard");
                } else if (os.contains("mac")) {
                    pb = new ProcessBuilder("pbcopy");
                } else if (os.contains("win")) {
                    pb = new ProcessBuilder("clip");
                } else {
                    throw new UnsupportedOperationException("Unsupported OS");
                }

                Process process = pb.start();
                process.getOutputStream().write(nbtString.getBytes());
                process.getOutputStream().close();
                process.waitFor();
                clipboardSuccess = true;
            } catch (Exception e) {
                // System command failed too
            }
        }

        return 1;
    }

    @Override
    public String getName() {
        return "bhwaila";
    }

    @Override
    public String getDescription() {
        return "Copies raw entity NBT data to clipboard";
    }
}