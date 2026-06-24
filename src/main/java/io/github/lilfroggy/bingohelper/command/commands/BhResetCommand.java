package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class BhResetCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bhreset")
                .executes(this::execute));
    }

    @Override
    public String getName() {
        return "bhreset";
    }

    @Override
    public String getDescription() {
        return "Resets the loaded guide";
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        Guide.reset();
        ChatLib.chat("§aReset guide!");
        return 1;
    }
}