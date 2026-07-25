package io.github.lilfroggy.bingohelper.guide;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.guide.deserializing.StepDeserializer;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.hud.HudDisplay;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Guide {
    private static final Step EXAMPLE_STEP = StepDeserializer.stepFromJson("{\"type\": \"message\",\"instruction\": \"&cRun &e/"+ GuideUpdater.UPDATE_COMMAND + " &cto import guide\",\"criteria\": \"kdasndlqwdn\"}");
    private static final GuideData EXAMPLE_GUIDE = new GuideData("Example", 1, 0, new Step[] {EXAMPLE_STEP}, "");
    private static final String COMPLETED_DISPLAY_FORMAT = "&b&l%s&r\n&aYou completed the guide!";
    private static final String ACTIVE_DISPLAY_FORMAT = "&b&l%s&r &7Step %s of %s&f%s&r";

    public static String name = EXAMPLE_GUIDE.name();
    public static int version = EXAMPLE_GUIDE.version();
    public static Step[] steps = EXAMPLE_GUIDE.steps();
    public static int stepIndex = EXAMPLE_GUIDE.stepIndex();
    public static boolean lerping = false;

    public static void init() {
        Events.JOIN_HYPIXEL.register(GuideUpdater::onJoinHypixel);
        Events.RENDER_HUD.register(Guide::onHudRender);
        GuideImporter.importFromSave();
        ActiveSteps.init();
    }

    public static final HudDisplay display = new HudDisplay("", "guide", () -> Config.guide);

    private static void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker tickDelta) {
        if (!Config.guide || !Skyblock.inBingo()) return;
        display.setString(getDisplayText());
        display.draw(graphics);
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
        lerping = false;
        GuideNavigator.reset();
    }

    public static String getDisplayText() {
        if (ActiveSteps.none()) {
            return String.format(COMPLETED_DISPLAY_FORMAT, name);
        }

        if (lerping) return String.format(
            ACTIVE_DISPLAY_FORMAT, 
            name, 
            stepIndex + 1, 
            steps.length, 
            ""
        );
    
        return String.format(
            ACTIVE_DISPLAY_FORMAT, 
            name, 
            stepIndex + 1, 
            steps.length, 
            ActiveSteps.getCombinedInstructions()
        );
    }

    public static void setIndex(int index) {
        if (index < 0) index = 0;
        if (index >= steps.length) index = steps.length;
        stepIndex = index;
        GuideSaver.saveUserProgress();
    }

    public static int index() {
        return stepIndex;
    }

    public static boolean isValidIndex(int index) {
        return index >= 0 && index < steps.length;
    }

    public static boolean isCompleted() {
        return steps == null || stepIndex >= steps.length;
    }

}