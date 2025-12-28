package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.data.Collections;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Map;

public class BhCollectionsCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("bhcollections")
                .then(ClientCommandManager.literal("list")
                        .executes(this::listCollections))
                .then(ClientCommandManager.literal("reset")
                        .executes(this::resetCollections)));
    }

    @Override
    public String getName() {
        return "bhcollections";
    }

    @Override
    public String getDescription() {
        return "Manage tracked collection levels";
    }

    private int listCollections(CommandContext<FabricClientCommandSource> context) {
        Map<String, Integer> allCollections = Collections.getCollections();

        if (allCollections.isEmpty()) {
            ChatLib.chatWithPrefix("§cNo collections tracked yet!");
            return 1;
        }

        ChatLib.chat("§6--- Tracked Collections ---");
        allCollections.forEach((id, level) -> {
            ChatLib.chat("§e" + id + ": §f" + level);
        });
        ChatLib.chat("§6----------------------");

        return 1;
    }

    private int resetCollections(CommandContext<FabricClientCommandSource> context) {
        Collections.reset();
        ChatLib.chatWithPrefix("§aAll collection data has been reset.");
        return 1;
    }
}