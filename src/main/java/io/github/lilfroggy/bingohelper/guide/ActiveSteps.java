package io.github.lilfroggy.bingohelper.guide;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderHudEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.guide.step.properties.prerequisites.PrerequisitesProperty;
import io.github.lilfroggy.bingohelper.hud.HudDisplay;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.JsonDataArray;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.PersistentData;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

public class ActiveSteps {
    private static final Comparator<Step> COMMAND_STEP_COMPARATOR = Comparator.comparingLong(Step::lastProgressMs);
    private static final String COMPLETED_DISPLAY_FORMAT = "&b&l%s&r\n&aYou completed the guide!";
    private static final String WITH_INFO_FORMAT = "&b&l%s&r &7Step %s of %s&f%s&r";
    private static final ClientTickEndEvent CLIENT_TICK_END = ActiveSteps::onClientTickEnd;
    private static final RenderHudEvent RENDER_HUD = ActiveSteps::onHudRender;
    private static final PersistentData state = new PersistentData("config/bingohelper/state.json", "{\"index\":0}");
    public static final HudDisplay display = new HudDisplay("", "guide", () -> Config.guide);
    private static final LinkedHashSet<Step> priority = new LinkedHashSet<>();
    private static final LinkedHashSet<Step> active = new LinkedHashSet<>();
    public static final String prefix = "\n&f";
    public static boolean dirty = false;

    public static void init() {
        Config.INSTANCE.registerListener("guide", state -> {
            if ((boolean) state) activateAll();
            else deactivateAll();
        });

        Config.INSTANCE.registerListener("instructionsOnly", state -> {
            dirty = true;
        });

        Events.JOIN_BINGO.register(ActiveSteps::onJoinBingo);
        Events.LEAVE_BINGO.register(ActiveSteps::onLeaveBingo);
    }

    private static void onJoinBingo() {
        Events.CLIENT_TICK_END.register(CLIENT_TICK_END);
        Events.RENDER_HUD.register(RENDER_HUD);
        activateAll();
    }

    private static void onLeaveBingo() {
        Events.CLIENT_TICK_END.unregister(CLIENT_TICK_END);
        Events.RENDER_HUD.unregister(RENDER_HUD);
        deactivateAll();
    }

    public static void save() {
        state.set("index", Guide.index());
    
        var activeArray = new JsonDataArray();
        active.forEach(step -> activeArray.add(step.state()));
        
        state.set("active", activeArray);
        state.save();
    }

    public static void load() {
        clear();

        int guideIndex = state.getInt("index");

        state.getOrCreateArray("active").forEachObject(state -> {
            int stepIndex = state.getInt("index");

            if (stepIndex != guideIndex) {
                Step step = Guide.steps[stepIndex];

                if (step.prerequisites instanceof PrerequisitesProperty prerequisites) {
                    prerequisites.index = state.getInt("prerequisites$index");
                }

                add(step);
            }
        });

        Guide.setIndex(guideIndex);
        GuideNavigator.goToStep(Guide.index());
    }

    public static void add(Step step) {
        if (step == null) return;
        if (active.contains(step)) return;
        active.add(step);
        step.activate();
        dirty = true;
        GuideSaver.saveUserProgress();
    }

    public static void remove(Step step) {
        if (step == null) return;
        step.deactivate();
        active.remove(step);
        setPriority(step, false);
        dirty = true;
        GuideSaver.saveUserProgress();
    }

    public static void removeAllEffectiveBefore(int index) {
        var toRemove = active.stream()
            .filter(step -> step.effectiveAt() < index);
            
        toRemove.forEach(ActiveSteps::remove);
    }

    public static void removeAllRegisteredAfter(int index) {
        var toRemove = active.stream()
            .filter(step -> step.registrationIndex() > index);
        
        toRemove.forEach(ActiveSteps::remove);
    }

    public static void activateAll() {
        active.forEach(Step::activate);
    }

    public static void deactivateAll() {
        active.forEach(Step::deactivate);
    }

    public static void setPriority(Step step, boolean isPriority) {
        step.isPriority = isPriority;
        if (isPriority) priority.addFirst(step);
        else priority.remove(step);
        dirty = true;
    }

    public static boolean isEmpty() {
        return active.isEmpty();
    }

    public static int priorityAmount() {
        return priority.size();
    }

    public static boolean anyBlocking() {
        return active.stream().anyMatch(Step::isBlocking);
    }

    @Nullable
    private static String findCommand(Collection<Step> steps, Predicate<Step> predicate) {
        return steps.stream()
            .filter(step -> step.command() != null && predicate.test(step))
            .max(COMMAND_STEP_COMPARATOR)
            .map(Step::command)
            .orElse(null);
    }

    @Nullable
    public static String getPriorityCommand() {
        return findCommand(priority, step -> true);
    }

    @Nullable
    public static String getBlockingCommand() {
        return findCommand(active, Step::isBlocking);
    }

    @Nullable
    public static String getAnyCommand() {
        return findCommand(active, step -> !step.isHidden());
    }

    @Nullable
    public static String getUnformattedCommand() {
        String command = getPriorityCommand();
        if (command != null) return command;
        
        command = getBlockingCommand();
        if (command != null) return command;
        
        return getAnyCommand();
    }

    @Nullable
    public static String getCommand() {
        String command = getUnformattedCommand();
        return command != null ? command.replaceAll("%visitIsland%", Config.visitIsland) : null;
    }

    public static void clear() {
        deactivateAll();
        active.clear();
        priority.clear();
    }

    private static final StringBuilder body = new StringBuilder();

    public static String getInstructions() {
        body.setLength(0);

        priority.forEach(step -> appendInstruction(step));

        boolean darken = body.isEmpty() ? false : true;

        for (Step step : active) {
            if (step.isHidden() || step.isPriority()) continue;
            if (!darken) appendInstruction(step);
            else appendGrayInstruction(step);
        }

        return Config.instructionsOnly ? body.toString().replaceFirst("\n", "") : body.toString();
    }

    private static void appendInstruction(Step step) {
        body.append(prefix).append(step.instruction());
    }

    private static void appendGrayInstruction(Step step) {
        body.append(prefix).append("&8").append(ChatLib.removeFormatting(step.instruction())).append("&r&f");
    }

    private static void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker tickDelta) {
        if (!Config.guide || !Skyblock.inBingo()) return;
        display.draw(graphics);
    }

    public static void updateDisplay() {
        if (!dirty) return;
        dirty = false;

        String newString = getDisplay();
        display.setString(newString);
        Logger.debug("updated guide display");
    }

    public static String getDisplay() {
        String instructions = getInstructions();

        if (active.isEmpty()) {
            return String.format(
                COMPLETED_DISPLAY_FORMAT,
                Guide.name
            );
        }
    
        return !Config.instructionsOnly ? String.format(
            WITH_INFO_FORMAT,
            Guide.name,
            Guide.stepIndex + 1,
            Guide.steps.length,
            instructions
        ) : instructions;
    }

    public static void onClientTickEnd(int tick) {
        updateDisplay();
    }
}