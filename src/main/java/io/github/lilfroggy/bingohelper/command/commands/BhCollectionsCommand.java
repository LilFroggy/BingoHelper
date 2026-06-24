package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.data.Collections;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Map;

public class BhCollectionsCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bhcollections")
            .then(ClientCommands.literal("list")
                .executes(this::listCollections))
            .then(ClientCommands.literal("reset")
                .executes(this::resetCollections))
            .then(ClientCommands.literal("set")
                .then(ClientCommands.argument("name", StringArgumentType.string())
                .then(ClientCommands.argument("level", IntegerArgumentType.integer())
                .executes(this::setCollection)))));
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
            ChatLib.chat("§cNo collections tracked yet!");
            return 1;
        }

        ChatLib.chatNoPrefix("§6--- Tracked Collections ---");
        allCollections.forEach((id, level) -> {
            ChatLib.chatNoPrefix("§e" + id + ": §f" + level);
        });
        ChatLib.chatNoPrefix("§6----------------------");

        return 1;
    }

    private int resetCollections(CommandContext<FabricClientCommandSource> context) {
        Collections.reset();
        ChatLib.chat("§aAll collection data has been reset.");
        return 1;
    }

    private int setCollection(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        Integer level = IntegerArgumentType.getInteger(context, "level");
    
        Collections.set(name, level);

        ChatLib.chat("§aSet collection §b" + name + " §ato level §b" + level);
    
        return 1;
    }
}