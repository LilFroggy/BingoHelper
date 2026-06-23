package io.github.lilfroggy.bingohelper.guide;

import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.messages.Messages;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.Scheduler;

public class GuideNavigator extends Guide {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static void reset() {
        ActiveSteps.clear();
        goToStep(0);
    }

    public static void advance(Step step) {
        ActiveSteps.remove(step);

        long currentTime = System.currentTimeMillis();
        long durationMillis = currentTime - stepStartTime;
        long seconds = durationMillis / 1000;

        String message = Messages.GUIDE_ADVANCE.formatted(
            step.instruction().replaceAll("\n", " ").replaceAll("&", "§"), 
            ChatLib.formatDuration(seconds)
        );

        Scheduler.SCHEDULER.schedule(() -> {
            CLIENT.execute(() -> {
                ChatLib.chat(message);
            });
        }, 250, TimeUnit.MILLISECONDS);

        if (ActiveSteps.anyBlocking()) return;

        goToStep(stepIndex + 1);
    }

    public static String skip() {
        return skip(1);
    }

    public static String skip(int amount) {
        if (isCompleted()) return Messages.GUIDE_SKIP_NONE;

        int targetIndex = stepIndex + amount;

        if (targetIndex >= steps.length) targetIndex = steps.length;

        goToStep(targetIndex);

        if (amount == 1) return Messages.GUIDE_SKIP_ONE;
        return Messages.GUIDE_SKIP_MULTIPLE.formatted(amount);
    }

    public static String back() {
        return back(1);
    }

    public static String back(int amount) {
        // If we are at the very beginning, we can't go back
        if (stepIndex == 0) return Messages.GUIDE_BACK_NONE;

        int targetIndex = stepIndex - amount;

        if (targetIndex < 0) targetIndex = 0;

        goToStep(targetIndex);

        if (amount == 1) return Messages.GUIDE_BACK_ONE;
        return Messages.GUIDE_BACK_MULTIPLE.formatted(amount);
    }
    
    public static void goToStep(int index) {

        if (index > stepIndex) {
            // Skipping forward: Clean up any background steps we are jumping past
            ActiveSteps.removeAllEffectiveBefore(index);
        } else {
            // Going backward: Safely unregister everything we are retreating through
            ActiveSteps.removeAllRegisteredAfter(index);
        }

        stepStartTime = System.currentTimeMillis();
    
        activateStep(index);
    }

    private static void activateStep(int index) {
        setIndex(index);
        if (!isValidIndex(index())) return;
        Step step = steps[index()];
        step.reset();

        ActiveSteps.add(step);

        if (!step.isBlocking()) {
            activateStep(index() + 1);
        }
    }
}