package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.guide.GuideImporter;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class BhImportCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bhimport")
                .executes(this::execute));
    }

    @Override
    public String getName() {
        return "bhimport";
    }

    @Override
    public String getDescription() {
        return "Imports guide from clipboard";
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        GuideImporter.importFromClipboard();
        return 1;
    }
}