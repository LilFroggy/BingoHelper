package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class BhBackCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bhback")
                .executes(this::backOne)
                .then(ClientCommands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(this::backAmount)));
    }

    @Override
    public String getName() {
        return "bhback";
    }

    @Override
    public String getDescription() {
        return "Go back a desired amount of steps: defaults to 1";
    }

    private int backOne(CommandContext<FabricClientCommandSource> context) {
        ChatLib.chat(Guide.back());
        return 1;
    }

    private int backAmount(CommandContext<FabricClientCommandSource> context) {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ChatLib.chat(Guide.back(amount));
        return 1;
    }
}