package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.hud.HudManager;

public class BhHudCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommands.literal("bhhud")
                        .executes(this::execute)
        );
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        HudManager.open();
        return 1;
    }

    @Override
    public String getName() {
        return "bhhud";
    }

    @Override
    public String getDescription() {
        return "Opens the hud editor";
    }
}