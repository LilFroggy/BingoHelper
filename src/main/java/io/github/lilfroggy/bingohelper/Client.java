package io.github.lilfroggy.bingohelper;

import io.github.lilfroggy.bingohelper.command.CommandHandler;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.data.Collections;
import io.github.lilfroggy.bingohelper.data.Skills;
import io.github.lilfroggy.bingohelper.features.PuzzlerSolver;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.KeyBindings;
import io.github.lilfroggy.bingohelper.util.Scoreboard;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.Tablist;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        Config.init(); // Load config before anything uses it

        PuzzlerSolver.init();
        Guide.init();
        Tablist.init();
        Scoreboard.init();
        Skills.init();
        Collections.init();
        Skyblock.init();
        KeyBindings.init();

        CommandHandler.init();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            CommandHandler.registerAll(dispatcher);
        });
    }
}