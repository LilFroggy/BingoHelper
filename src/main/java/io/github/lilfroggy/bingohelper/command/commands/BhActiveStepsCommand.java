package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.guide.ActiveSteps;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Logger;

import java.util.List;

public class BhActiveStepsCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommands.literal("bhactivesteps")
                        .executes(context -> execute(context, null))
                        .then(ClientCommands.argument("index", IntegerArgumentType.integer(0))
                                .executes(context -> execute(context, IntegerArgumentType.getInteger(context, "index"))))
        );
    }

    private int execute(CommandContext<FabricClientCommandSource> context, Integer targetIndex) {
        StringBuilder sb = new StringBuilder("\n=== Active Steps Diagnostics ===\n");
        if (targetIndex != null) {
            sb.append("Filter Index: ").append(targetIndex).append("\n");
        }

        try {
            List<Step> activeSteps = ActiveSteps.active();

            if (activeSteps.isEmpty()) {
                sb.append("No active steps currently found.\n");
            } else {
                boolean found = false;
                for (Step step : activeSteps) {
                    if (targetIndex != null && step.index != targetIndex) {
                        continue;
                    }

                    found = true;
                    sb.append("Step (Index ").append(step.index).append("):\n");
                    sb.append("  └── ").append(step.toString()).append("\n");
                }

                if (targetIndex != null && !found) {
                    sb.append("No active step found with index ").append(targetIndex).append(".\n");
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to dump active steps: ", e);
        }

        sb.append("==================================");
        Logger.info(sb.toString());
        return 1;
    }

    @Override
    public String getName() {
        return "bhactivesteps";
    }

    @Override
    public String getDescription() {
        return "Lists active steps, optionally filtered by step index";
    }
}