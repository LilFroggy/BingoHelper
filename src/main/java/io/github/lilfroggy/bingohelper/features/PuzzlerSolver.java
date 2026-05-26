package io.github.lilfroggy.bingohelper.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvent;
import io.github.lilfroggy.bingohelper.util.render.RenderingEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class PuzzlerSolver {
    private static final RenderingEvent RENDER = PuzzlerSolver::render;

    private static final Pattern PUZZLER_BEGIN_PATTERN = Pattern.compile("^\\[NPC] Puzzler: ((?:▲|▶|◀|▼){10})$");
    private static final Pattern PUZZLER_END_PATTERN = Pattern.compile("^\\[NPC] Puzzler: ▶▶Nice!  ▲Here, ◀have ▼some◀ ▶Mithril Powder!▲$");

    private static final float[] SOLUTION_FILL_COLOR = {0.0f, 1.0f, 0.0f, 0.5f};
    private static final float[] SOLUTION_OUTLINE_COLOR = {0.0f, 1.0f, 0.0f, 1.0f};

    private static Vec3d solution = null;
    private static Vec3d tracePos = null;

    public static void init() {
        Events.MESSAGE.register(PuzzlerSolver::onGameMessage);
        Events.CHANGE_WORLD.register(PuzzlerSolver::onWorldChange);
    }

    public static void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!Config.puzzlerSolver) return;
        if (!"Dwarven Mines".equals(Skyblock.area())) return;

        if (PUZZLER_END_PATTERN.matcher(unformattedMsg).matches()) {
            clearSolution();
            return;
        }

        if (solution != null && tracePos != null) return;

        Matcher beginMatcher = PUZZLER_BEGIN_PATTERN.matcher(unformattedMsg);
        if (beginMatcher.matches()) updateSolution(beginMatcher.group(1));
    }

    private static void updateSolution(String msg) {
        int x = 181;
        int z = 135;
        for (char c : msg.toCharArray()) {
            if (c == '▲') z++;
            else if (c == '▼') z--;
            else if (c == '◀') x++;
            else if (c == '▶') x--;
        }
        solution = new Vec3d(x, 195, z);
        tracePos = new Vec3d(x + 0.5, 196.01, z + 0.5);
        RenderingEvents.LINE.register(RENDER);
    }

    private static void clearSolution() {
        RenderingEvents.LINE.unregister(RENDER);
        solution = null;
        tracePos = null;
    }

    public static void render(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        if (solution == null || tracePos == null) return;
        RenderLib.renderFilledAndOutline(Box.from(solution), SOLUTION_OUTLINE_COLOR, SOLUTION_FILL_COLOR);
        RenderLib.renderLineFromCursor(context, tracePos, RenderLib.MINECRAFT_GREEN);
    }

    public static void onWorldChange(MinecraftClient client, ClientWorld world) {
        clearSolution();
    }
}