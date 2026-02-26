package io.github.lilfroggy.bingohelper.guide;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.CreateBingoProfileEventBus;
import io.github.lilfroggy.bingohelper.events.JoinHypixelEventBus;
import io.github.lilfroggy.bingohelper.events.HudRenderEventBus;
import io.github.lilfroggy.bingohelper.events.JoinBingoEventBus;
import io.github.lilfroggy.bingohelper.events.LeaveBingoEventBus;
import io.github.lilfroggy.bingohelper.guide.steps.*;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class Guide {
    private static final Step EXAMPLE_STEP = StepParser.stepFromJson("{\"type\": \"message\",\"instruction\": \"&cRun &e/bhupdateguide &cto import guide\",\"criteria\": \"kdasndlqwdn\"}");
    private static final GuideData EXAMPLE_GUIDE = new GuideData("Example", 1, 0, new Step[] {EXAMPLE_STEP}, "");
    private static final String COMPLETED_DISPLAY_FORMAT = "&b&l%s&r\n&aYou completed the guide!";
    private static final String ACTIVE_DISPLAY_FORMAT = "&b&l%s&r &7Step %s of %s\n%s";

    public static String name = EXAMPLE_GUIDE.name();
    public static int version = EXAMPLE_GUIDE.version();
    public static Step[] steps = EXAMPLE_GUIDE.steps();
    public static Step currentStep = EXAMPLE_GUIDE.steps()[0];
    public static int stepIndex = EXAMPLE_GUIDE.stepIndex();
    public static long stepStartTime = System.currentTimeMillis();
    public static boolean completed = false;

    static {
        HudRenderEventBus.register(Guide::onHudRender);
        CreateBingoProfileEventBus.register(Guide::onCreateBingoProfile);
        JoinBingoEventBus.register(Guide::onJoinBingo);
        LeaveBingoEventBus.register(Guide::onLeaveBingo);
    }

    public static void init() {
        JoinHypixelEventBus.register(GuideUpdater::onJoinHypixel);
        GuideImporter.importFromSave();
    }

    private static void onHudRender(DrawContext drawContext, RenderTickCounter tickDelta) {
        if (!Config.guide || !Skyblock.inBingo() || currentStep == null) return;
        RenderLib.drawFormattedString(drawContext, getDisplayText(), 10, 10);
    }

    private static void onCreateBingoProfile() {
        if (Config.debug) Logger.info("Created bingo profile");
        GuideUpdater.check(Config.autoImport);
        Guide.reset();
    }

    private static void onJoinBingo() {
        currentStep.activate();
    }

    private static void onLeaveBingo() {
        currentStep.deactivate();
    }

    public static void advance() {
        GuideNavigator.advance();
    }

    public static String skip() {
        return GuideNavigator.skip();
    }

    public static String skip(int amount) {
        return GuideNavigator.skip(amount);
    }

    public static String back() {
        return GuideNavigator.back();
    }

    public static String back(int amount) {
        return GuideNavigator.back(amount);
    }

    public static void reset() {
        GuideNavigator.reset();
    }

    public static String getDisplayText() {
        return String.format(completed ? COMPLETED_DISPLAY_FORMAT : ACTIVE_DISPLAY_FORMAT, name, stepIndex + 1, steps.length, currentStep.formattedInstruction());
    }

}