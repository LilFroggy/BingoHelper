package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.events.EventHandler;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.util.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BhActiveListenersCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommands.literal("bhactivelisteners")
                        .executes(context -> execute(context, null))
                        .then(ClientCommands.argument("package filter", StringArgumentType.word())
                                .executes(context -> execute(context, StringArgumentType.getString(context, "package filter"))))
        );
    }

    private int execute(CommandContext<FabricClientCommandSource> context, String packageFilter) {
        StringBuilder sb = new StringBuilder("\n=== Event Registry Diagnostics ===\n");
        if (packageFilter != null && !packageFilter.isEmpty()) {
            sb.append("Filter: ").append(packageFilter).append("\n");
        }

        try {
            for (Field field : Events.class.getDeclaredFields()) {
                if (EventHandler.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    EventHandler<?> handler = (EventHandler<?>) field.get(null);

                    if (handler != null) {
                        List<?> listeners = handler.getListeners();

                        if (listeners.isEmpty()) {
                            continue;
                        }

                        List<String> matchedLines = new ArrayList<>();
                        for (Object listener : listeners) {
                            String className = listener.getClass().getName();
                            className = className.replace("io.github.lilfroggy.bingohelper.", "");

                            if (packageFilter == null || packageFilter.isEmpty() || className.contains(packageFilter)) {
                                matchedLines.add("  └── " + className + "\n");
                            }
                        }

                        if (packageFilter != null && !packageFilter.isEmpty() && matchedLines.isEmpty()) {
                            continue;
                        }

                        sb.append("Event: ").append(field.getName())
                          .append(" (").append(listeners.size()).append(" listeners)\n");

                        for (Object listener : listeners) {
                            String className = listener.getClass().getName();
                            className = className.replace("io.github.lilfroggy.bingohelper.", "");

                            if (packageFilter != null && !packageFilter.isEmpty() && !className.contains(packageFilter)) {
                                continue;
                            }

                            sb.append("  └── ").append(className).append("\n");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to dump event registry: ", e);
        }

        sb.append("==================================");
        Logger.info(sb.toString());
        return 1;
    }

    @Override
    public String getName() {
        return "bhactivelisteners";
    }

    @Override
    public String getDescription() {
        return "Lists all active listeners";
    }
}