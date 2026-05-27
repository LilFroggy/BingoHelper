package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class BhCopyNbt implements ClientCommand {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("bhcopynbt")
                .executes(this::execute));
    }

    @Override
    public String getName() {
        return "bhcopynbt";
    }

    @Override
    public String getDescription() {
        return "Copies the NBT of the item in your hand to the clipboard";
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        if (!(CLIENT.player instanceof ClientPlayerEntity player)) return 0;
        ItemStack item = player.getMainHandStack();
        if (item == null) {
            ChatLib.chat("§cNo item in hand!");
            return 0;
        }
        NbtCompound nbt = Skyblock.getNbt(item);
        if (nbt == null) {
            ChatLib.chat("§cItem seems to not have NBT!");
            return 0;
        }
        ClipboardUtils.setClipboard(nbt.toString());
        ChatLib.chat("§aNBT copied to clipboard!");
        return 1;
    }
}