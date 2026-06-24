package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.guide.GuideSaver;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
import io.github.lilfroggy.bingohelper.util.FileLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class BhExportCommand implements ClientCommand {
    
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bhexport")
                .executes(this::execute));
    }

    @Override
    public String getName() {
        return "bhexport";
    }

    @Override
    public String getDescription() {
        return "Copies the current guide to clipboard";
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        String guide = FileLib.read(GuideSaver.ACTIVE_SAVE_PATH);
        if (guide == null) return 1;
        ClipboardUtils.setClipboard(guide);
        ChatLib.chat("§aCopied guide to clipboard!");
        return 1;
    }
}