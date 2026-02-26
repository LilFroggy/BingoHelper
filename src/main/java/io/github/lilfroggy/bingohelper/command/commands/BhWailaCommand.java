package io.github.lilfroggy.bingohelper.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.lilfroggy.bingohelper.command.ClientCommand;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class BhWailaCommand implements ClientCommand {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("bhwaila")
                .executes(this::execute));
    }

    private int execute(CommandContext<FabricClientCommandSource> context) {
        HitResult hitResult = CLIENT.crosshairTarget;

        if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
            ChatLib.chat("§cNot looking at an entity!");
            return 0;
        }

        EntityHitResult entityHitResult = (EntityHitResult) hitResult;
        Entity entity = entityHitResult.getEntity();

        NbtWriteView view = NbtWriteView.create(ErrorReporter.EMPTY);
        entity.writeData(view);

        Text nbt = NbtHelper.toPrettyPrintedText(view.getNbt());

        ClipboardUtils.setClipboard(nbt.getString());
        ChatLib.chatNoPrefix(nbt);

        return 1;
    }

    @Override
    public String getName() {
        return "bhwaila";
    }

    @Override
    public String getDescription() {
        return "Copies the NBT data of the entity you are looking at";
    }
}