package io.github.lilfroggy.bingohelper.command.commands;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class BhCopyEnchants implements ClientCommand {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("bhcopyenchants")
                .executes(this::execute));
    }

    @Override
    public String getName() {
        return "bhcopyenchants";
    }

    @Override
    public String getDescription() {
        return "Copies the enchants of the item in your hand to the clipboard";
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        ItemStack item = CLIENT.player.getMainHandStack();
        if (item == null) {
            ChatLib.chat("§cNo item in hand!");
            return 0;
        }
        List<String> enchants = Skyblock.getEnchants(item);
        if (enchants == null) {
            ChatLib.chat("§cThis item seems to not have any enchants!");
            return 0;
        }
        ClipboardUtils.setClipboard(enchants.toString());
        ChatLib.chat("§aEnchants copied to clipboard!");
        return 1;
    }
}