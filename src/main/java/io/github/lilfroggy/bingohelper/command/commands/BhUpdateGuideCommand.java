package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.guide.GuideUpdater;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class BhUpdateGuideCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bhupdateguide")
                .executes(this::updateGuide));
    }

    @Override
    public String getName() {
        return "bhupdateguide";
    }

    @Override
    public String getDescription() {
        return "Imports latest official guide";
    }

    private int updateGuide(CommandContext<FabricClientCommandSource> context) {
        GuideUpdater.check(true);
        return 1;
    }
} 