package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.data.MobTypes;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Set;

public class BhMobTypesCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bhmobtypes")
                .then(ClientCommands.literal("list")
                        .executes(this::listMobTypes))
                .then(ClientCommands.literal("reset")
                        .executes(this::resetMobTypes)));
    }

    @Override
    public String getName() {
        return "bhmobtypes";
    }

    @Override
    public String getDescription() {
        return "Manage tracked mob types";
    }

    private int listMobTypes(CommandContext<FabricClientCommandSource> context) {
        Set<String> allMobTypes = MobTypes.getUnlocked();

        if (allMobTypes.isEmpty()) {
            ChatLib.chat("§cNo mob types tracked yet!");
            return 1;
        }

        ChatLib.chatNoPrefix("§6--- Tracked Mob Types ---");
        allMobTypes.forEach(unlocked -> {
            ChatLib.chatNoPrefix("§e" + unlocked);
        });
        ChatLib.chatNoPrefix("§6-----------------------");

        return 1;
    }

    private int resetMobTypes(CommandContext<FabricClientCommandSource> context) {
        MobTypes.reset();
        ChatLib.chat("§aAll mob type data has been reset.");
        return 1;
    }
}