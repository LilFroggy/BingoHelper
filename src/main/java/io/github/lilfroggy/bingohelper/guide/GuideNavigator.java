package io.github.lilfroggy.bingohelper.guide;

import java.util.concurrent.TimeUnit;

import io.github.lilfroggy.bingohelper.messages.Messages;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.Scheduler;
import net.minecraft.client.MinecraftClient;

public class GuideNavigator {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void reset() {
        goToStep(0);
    }

    public static void advance() {
        Guide.currentStep.deactivate();
        if (Guide.completed) return;
        //if (Config.exitMenus && CLIENT.player != null) CLIENT.setScreen(null);
        long currentTime = System.currentTimeMillis();
        long durationMillis = currentTime - Guide.stepStartTime;
        long seconds = durationMillis / 1000;
        String message = Messages.GUIDE_ADVANCE.formatted(Guide.currentStep.formattedInstruction().replaceAll("\n", " ").replaceAll("&", "§"), ChatLib.formatDuration(seconds));
        Scheduler.SCHEDULER.schedule(() -> {
            CLIENT.execute(() -> {
                ChatLib.chatWithPrefix(message);
            });
        }, 250, TimeUnit.MILLISECONDS);
        skip();
    }

    public static String skip() {
        return skip(1);
    }

    public static String skip(int amount) {
        if (Guide.completed) return Messages.GUIDE_SKIP_NONE;
        int max = Guide.steps.length - 1 - Guide.stepIndex;
        if (!Guide.completed) max++;
        int actual = Math.min(amount, max);
        goToStep(Guide.stepIndex + actual);
        if (actual == 1) return Messages.GUIDE_SKIP_ONE;
        else return Messages.GUIDE_SKIP_MULTIPLE.formatted(actual);
    }

    public static String back() {
        return back(1);
    }

    public static String back(int amount) {
        if (Guide.stepIndex == 0 && !Guide.completed) return Messages.GUIDE_BACK_NONE;
        int currentIndex = Guide.completed ? Guide.stepIndex + 1 : Guide.stepIndex;
        int max = Guide.stepIndex;
        if (Guide.completed) max++;
        int actual = Math.min(amount, max);
        goToStep(currentIndex - actual);
        if (actual == 1) return Messages.GUIDE_BACK_ONE;
        else return Messages.GUIDE_BACK_MULTIPLE.formatted(actual);
    }
    
    public static void goToStep(int index) {
        Guide.completed = isIndexToolarge(index);
        Guide.currentStep.deactivate();
        if (Guide.completed) return;
        setCurrentStep(index);
    }

    private static void setCurrentStep(int index) {
        Guide.stepIndex = isIndexToolarge(index) ? lastStepIndex() : isIndexTooSmall(index) ? 0 : index;
        GuideSaver.saveProgress();
        Guide.currentStep = Guide.steps[index];
        Guide.currentStep.reset();
        if (Guide.completed) return;
        Guide.currentStep.activate();
    }

    private static boolean isIndexTooSmall(int index) {
        return index < 0;
    }

    private static boolean isIndexToolarge(int index) {
        return index >= Guide.steps.length;
    }

    private static int lastStepIndex() {
        return Guide.steps.length - 1;
    }
}