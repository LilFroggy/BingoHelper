package io.github.lilfroggy.bingohelper.command.commands;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.util.ChatLib;

public class BhSimulateCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommands.literal("bhsimulate")
                        .then(ClientCommands.argument("message", StringArgumentType.greedyString())
                                .executes(this::execute))
        );
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        String message = StringArgumentType.getString(context, "message");

        String formatted = ChatLib.replaceAmpersands(message);
        String unformatted = ChatLib.removeFormatting(formatted);
        CallbackInfo callbackInfo = new CallbackInfo("simulate", true);

        ChatLib.chatNoPrefix(formatted);
        Events.MESSAGE.invoke(listener -> listener.onMessage(formatted, unformatted, callbackInfo));

        return 1;
    }

    @Override
    public String getName() {
        return "bhsimulate";
    }

    @Override
    public String getDescription() {
        return "Simulates a chat message";
    }
}