package io.github.lilfroggy.bingohelper.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import io.github.lilfroggy.bingohelper.command.commands.*;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    private static final List<ClientCommand> commands = new ArrayList<>();

    public static void init() {
        registerCommand(new BhCommand());
        registerCommand(new BhHudCommand());
        registerCommand(new BhImportCommand());
        registerCommand(new BhExportCommand());
        registerCommand(new BhSkipCommand());
        registerCommand(new BhBackCommand());
        registerCommand(new BhResetCommand());
        registerCommand(new BhWailaCommand());
        registerCommand(new BhUpdateGuideCommand());
        registerCommand(new BhSimulateCommand());

        registerCommand(new BhSkillsCommand());
        registerCommand(new BhCollectionsCommand());
        registerCommand(new BhMobTypesCommand());

        registerCommand(new BhCopyNbt());
        registerCommand(new BhCopyEnchants());
    }

    public static void registerCommand(ClientCommand command) {
        commands.add(command);
    }

    public static void registerAll(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        for (ClientCommand command : commands) {
            command.register(dispatcher);
        }
    }

    public static List<ClientCommand> getCommands() {
        return new ArrayList<>(commands);
    }
    
    public static boolean exists(String command) {
        var dispatcher = ClientCommands.getActiveDispatcher();
        if (dispatcher == null) return false;
        return dispatcher.getRoot().getChild(command) != null;
    }
}