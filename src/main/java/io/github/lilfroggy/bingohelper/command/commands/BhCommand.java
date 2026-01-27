package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.config.Config;

public class BhCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("bh")
                        .executes(this::executeNoArgs)
        );
        dispatcher.register(
                ClientCommandManager.literal("bingohelper")
                        .executes(this::executeNoArgs)
        );
    }

    private int executeNoArgs(CommandContext<FabricClientCommandSource> context) {
        Config.open();
        return 1;
    }

    @Override
    public String getName() {
        return "bh";
    }

    @Override
    public String getDescription() {
        return "Opens the config menu";
    }
}