package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.guide.GuideUpdater;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class BhUpdateCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("bhupdate")
                .executes(this::updateGuide));
    }

    @Override
    public String getName() {
        return "bhupdate";
    }

    @Override
    public String getDescription() {
        return "Fetches latest repo guide";
    }

    private int updateGuide(CommandContext<FabricClientCommandSource> context) {
        GuideUpdater.update();
        return 1;
    }
} 