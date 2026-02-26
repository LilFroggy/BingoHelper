package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.data.Skills;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Map;

public class BhSkillsCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("bhskills")
                .then(ClientCommandManager.literal("list")
                        .executes(this::listSkills))
                .then(ClientCommandManager.literal("reset")
                        .executes(this::resetSkills)));
    }

    @Override
    public String getName() {
        return "bhskills";
    }

    @Override
    public String getDescription() {
        return "Manage tracked skill levels";
    }

    private int listSkills(CommandContext<FabricClientCommandSource> context) {
        Map<String, Double> allSkills = Skills.getSkills();

        if (allSkills.isEmpty()) {
            ChatLib.chat("§cNo skills tracked yet!");
            return 1;
        }

        ChatLib.chatNoPrefix("§6--- Tracked Skills ---");
        allSkills.forEach((id, level) -> {
            ChatLib.chatNoPrefix("§e" + id + ": §f" + level);
        });
        ChatLib.chatNoPrefix("§6----------------------");

        return 1;
    }

    private int resetSkills(CommandContext<FabricClientCommandSource> context) {
        Skills.reset();
        ChatLib.chat("§aAll skill data has been reset.");
        return 1;
    }
}