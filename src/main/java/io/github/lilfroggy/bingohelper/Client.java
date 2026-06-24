package io.github.lilfroggy.bingohelper;

import io.github.lilfroggy.bingohelper.command.CommandHandler;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.data.Collections;
import io.github.lilfroggy.bingohelper.data.MobTypes;
import io.github.lilfroggy.bingohelper.data.Skills;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.features.PuzzlerSolver;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.update.UpdateManager;
import io.github.lilfroggy.bingohelper.util.Bingo;
import io.github.lilfroggy.bingohelper.util.KeyBindings;
import io.github.lilfroggy.bingohelper.util.Scoreboard;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.Tablist;
import io.github.lilfroggy.bingohelper.util.render.GlowingEntities;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public class Client implements ClientModInitializer {

    private static int currentTick = 0;

    @Override
    public void onInitializeClient() {

        // Events

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            currentTick++;
            Events.CLIENT_TICK_START.invoke(listener -> listener.onClientTickStart(currentTick));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Events.CLIENT_TICK_END.invoke(listener -> listener.onClientTickEnd(currentTick));
        });

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((Minecraft client, ClientLevel world) -> {
            Events.CHANGE_WORLD.invoke(listener -> listener.onWorldChange(client, world));
        });

        LevelRenderEvents.END_MAIN.register(context -> {
            Events.RENDER_WORLD.invoke(listener -> listener.onRenderWorld(context.poseStack(), context.bufferSource(), context));
        });

        HudElementRegistry.addLast(BingoHelper.id("hud"), (context, tickCounter) -> {
            Events.RENDER_HUD.invoke(listener -> listener.onRenderHud(context, tickCounter));
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.remove(screen).register((removedScreen) -> {
                Events.CLOSE_SCREEN.invoke(listener -> listener.onScreenClose(removedScreen));
            });
        });

        RenderingEvents.init();

        Config.init(); // Load config before anything uses it

        GlowingEntities.init();
        Bingo.init();
        UpdateManager.init();
        PuzzlerSolver.init();
        Guide.init();
        Tablist.init();
        Scoreboard.init();
        MobTypes.init();
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