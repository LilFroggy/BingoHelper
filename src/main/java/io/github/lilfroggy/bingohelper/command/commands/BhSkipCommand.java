package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class BhSkipCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("bhskip")
                .executes(this::skipOne)
                .then(ClientCommandManager.argument("amount", IntegerArgumentType.integer(1))
                        .executes(this::skipAmount)));
    }

    @Override
    public String getName() {
        return "bhskip";
    }

    @Override
    public String getDescription() {
        return "Skip a desired amount of steps: defaults to 1";
    }

    private int skipOne(CommandContext<FabricClientCommandSource> context) {
        ChatLib.chat(Guide.skip());
        return 1;
    }

    private int skipAmount(CommandContext<FabricClientCommandSource> context) {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ChatLib.chat(Guide.skip(amount));
        return 1;
    }
}