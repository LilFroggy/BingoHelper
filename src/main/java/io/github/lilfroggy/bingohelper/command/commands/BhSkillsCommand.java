package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.data.Skills;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Map;

public class BhSkillsCommand implements ClientCommand {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bhskills")
            .then(ClientCommands.literal("list")
                .executes(this::listSkills))
            .then(ClientCommands.literal("reset")
                .executes(this::resetSkills))
            .then(ClientCommands.literal("set")
                .then(ClientCommands.argument("name", StringArgumentType.string())
                .then(ClientCommands.argument("level", DoubleArgumentType.doubleArg())
                .executes(this::setSkill)))));
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

    private int setSkill(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        double level = DoubleArgumentType.getDouble(context, "level");
    
        Skills.set(name, level);

        ChatLib.chat("§aSet skill §b" + name + " §ato level §b" + level);
    
        return 1;
    }
}