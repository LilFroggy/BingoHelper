package io.github.lilfroggy.bingohelper.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

/**
 * Interface for all client-side commands
 */
public interface ClientCommand {
    /**
     * Register the command with the dispatcher
     * @param dispatcher The command dispatcher
     */
    void register(CommandDispatcher<FabricClientCommandSource> dispatcher);

    /**
     * Get the name of the command
     * @return The command name
     */
    String getName();

    /**
     * Get the description of the command
     * @return The command description
     */
    default String getDescription() {
        return "No description provided";
    }
}