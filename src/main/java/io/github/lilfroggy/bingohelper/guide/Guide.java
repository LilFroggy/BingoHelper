package io.github.lilfroggy.bingohelper.guide;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.guide.deserializing.StepDeserializer;
import io.github.lilfroggy.bingohelper.guide.step.Step;

public class Guide {
    private static final Step EXAMPLE_STEP = StepDeserializer.stepFromJson("{\"type\": \"message\",\"instruction\": \"&cRun &e/"+ GuideUpdater.UPDATE_COMMAND + " &cto import guide\",\"criteria\": \"kdasndlqwdn\"}");
    private static final GuideData EXAMPLE_GUIDE = new GuideData("Example", 1, 0, new Step[] {EXAMPLE_STEP}, "");

    public static String name = EXAMPLE_GUIDE.name();
    public static int version = EXAMPLE_GUIDE.version();
    public static Step[] steps = EXAMPLE_GUIDE.steps();
    public static int stepIndex = EXAMPLE_GUIDE.stepIndex();

    public static void init() {
        Events.JOIN_HYPIXEL.register(GuideUpdater::onJoinHypixel);
        GuideImporter.importFromSave();
        ActiveSteps.init();
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