package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
import io.github.lilfroggy.bingohelper.util.FileLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class BhExportCommand implements ClientCommand {

    private static final String SAVE_FILE_PATH = "config/bingohelper/guide.json";

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("bhexport")
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
        String guide = FileLib.read(SAVE_FILE_PATH);
        if (guide == null) return 1;
        ClipboardUtils.setClipboard(guide);
        ChatLib.chatWithPrefix("§aCopied guide to clipboard!");
        return 1;
    }
}