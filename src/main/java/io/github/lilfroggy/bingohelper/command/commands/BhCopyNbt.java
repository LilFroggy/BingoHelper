package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class BhCopyNbt implements ClientCommand {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bhcopynbt")
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
        if (!(CLIENT.player instanceof LocalPlayer player)) return 0;
        ItemStack item = player.getMainHandItem();
        if (item.isEmpty()) {
            ChatLib.chat("§cNo item in hand!");
            return 0;
        }
        CompoundTag nbt = Skyblock.getNbt(item);
        if (nbt == null) {
            ChatLib.chat("§cItem seems to not have NBT!");
            return 0;
        }
        ClipboardUtils.setClipboard(nbt.toString());
        ChatLib.chat("§aNBT copied to clipboard!");
        return 1;
    }
}