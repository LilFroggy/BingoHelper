package io.github.lilfroggy.bingohelper.guide;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

public class ActiveSteps {
    private static final Set<Step> active = ConcurrentHashMap.newKeySet();
    private static final Set<Step> priority = ConcurrentHashMap.newKeySet();

    public static void init() {
        Events.JOIN_BINGO.register(ActiveSteps::onJoinBingo);
        Events.LEAVE_BINGO.register(ActiveSteps::onLeaveBingo);
    }

    private static void onJoinBingo() {
        activateAll();
    }

    private static void onLeaveBingo() {
        deactivateAll();
    }

    public static void save() {
        try {
            String activeIndicesStream = active.stream()
                .map(step -> String.valueOf(step.registrationIndex()))
                .collect(Collectors.joining(","));

            Config.activeIndices = activeIndicesStream;
            Config.save();
        } catch (Exception e) {
            Logger.error("Failed to save guide progress", e);
        }
    }

    public static void load() {
        clear();

        String activeIndices = Config.activeIndices;

        if (activeIndices != null && !activeIndices.isBlank()) {
            try {
                Arrays.stream(activeIndices.split(","))
                    .mapToInt(Integer::parseInt)
                    .filter(index -> index != Config.savedIndex)
                    .forEach(index -> add(Guide.steps[index]));
            } catch (Exception e) {
                Logger.error("Corrupted active indices config string: " + activeIndices, e);
            }
        }

        Guide.setIndex(Config.savedIndex);
        GuideNavigator.goToStep(Guide.index());
    }

    public static void add(Step step) {
        if (step == null) return;
        active.add(step);
        step.activate();
    }

    public static void remove(Step step) {
        if (step == null) return;
        step.deactivate();
        active.remove(step);
    }

    public static void removeAllEffectiveBefore(int index) {
        List<Step> toRemove = active.stream()
            .filter(step -> step.effectiveIndex() < index)
            .toList();

        toRemove.forEach(ActiveSteps::remove);
    }

    public static void removeAllRegisteredAfter(int index) {
        List<Step> toRemove = active.stream()
            .filter(step -> step.registrationIndex() > index)
            .toList();

        toRemove.forEach(ActiveSteps::remove);
    }

    public static void activateAll() {
        active.forEach(Step::activate);
    }

    public static void deactivateAll() {
        active.forEach(Step::deactivate);
    }

    public static Set<Step> getInternalSet() {
        return active;
    }

    public static boolean none() {
        return active.isEmpty();
    }

    public static boolean anyBlocking() {
        return active.stream().anyMatch(Step::isBlocking);
    }

    @Nullable
    public static Step getBlockingStep() {
        for (Step step : active) {
            if (step.isBlocking()) {
                return step;
            }
        }
        return null;
    }

    @Nullable
    public static String getPriorityCommand() {
        for (Step step : priority) {
            if (step.command != null) {
                return step.command;
            }
        }
        return null;
    }

    @Nullable
    public static String getBlockingCommand() {
        for (Step step : active) {
            if (step.isBlocking() && step.command != null) {
                return step.command;
            }
        }
        return null;
    }

    @Nullable
    public static String getAnyNonPriorityCommand() {
        for (Step step : active) {
            if (!step.isPriority() && step.command != null) {
                return step.command;
            }
        }
        return null;
    }

    @Nullable
    public static String getUnformattedCommand() {
        if (!priority.isEmpty()) return getPriorityCommand();
        String blockingCommand = getBlockingCommand();
        if (blockingCommand != null) return blockingCommand;
        return getAnyNonPriorityCommand();
    }

    @Nullable
    public static String getCommand() {
        String command = getUnformattedCommand();
        if (command == null) return command;
        return command.replaceAll("%visitIsland%", Config.visitIsland);
    }

    public static boolean anyOutlineEntityExists() {
        return active.stream().anyMatch(step -> {
            if (step.outlineEntities == null) return false;
            return step.outlineEntities.stream().anyMatch(outlinEntity -> outlinEntity.hasMatch());
        });
    }

    public static void clear() {
        deactivateAll();
        active.clear();
        priority.clear();
    }

    public static Set<Step> prioritySteps() {
        priority.clear();
        for (Step step : active) {
            if (step.isPriority()) {
                priority.add(step);
            }
        }
        return priority;
    }

    public static String getCombinedInstructions() {
        String priorityBody = "";

        if (!prioritySteps().isEmpty()) {
            for (Step step : priority) {
                priorityBody += "\n&f" + step.instruction();
            }
            return priorityBody;
        }

        String blockingBody = "";
        String blockingAsyncBody = "";
        String asyncBody = "";
    
        for (Step step : ActiveSteps.getInternalSet()) {
            if (step.isHidden()) continue;
            if (step.isBlocking() && !step.isAsync()) {
                blockingBody += "\n&f" + step.instruction();
            } else if (step.isBlocking()) {
                blockingAsyncBody += "\n&f" + step.instruction();
            } else {
                asyncBody += "\n&f" + step.instruction();
            }
        }
    
        return blockingBody.toString() + blockingAsyncBody.toString() + asyncBody.toString();
    }
}