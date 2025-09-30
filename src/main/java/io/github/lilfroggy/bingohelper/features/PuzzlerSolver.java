package io.github.lilfroggy.bingohelper.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.AreaChangeEventBus;
import io.github.lilfroggy.bingohelper.events.ChatEventBus;
import io.github.lilfroggy.bingohelper.events.WorldRenderEventBus;
import io.github.lilfroggy.bingohelper.util.Location;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class PuzzlerSolver {
    private static final Pattern PUZZLER_BEGIN_PATTERN = Pattern.compile("^\\[NPC] Puzzler: ((?:▲|▶|◀|▼){10})$");
    private static final Pattern PUZZLER_END_PATTERN = Pattern.compile("^\\[NPC] Puzzler: ▶▶Nice!  ▲Here, ◀have ▼some◀ ▶Mithril Powder!▲$");
    private static final float[] SOLUTION_COLOR = {0.0f, 1.0f, 0.0f};

    private static Vec3d solution = null;
    private static Vec3d tracePos = null;

    public static void init() {
        ChatEventBus.register(PuzzlerSolver::onGameMessage);
        AreaChangeEventBus.register(PuzzlerSolver::onAreaChange);
    }

    public static void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) return;
        if (!Config.puzzlerSolver) return;
        if (!"Dwarven Mines".equals(Location.AREA)) return;

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
        tracePos = new Vec3d(x+0.5, 196.01, z+0.5);
        WorldRenderEventBus.register(PuzzlerSolver::onWorldRender);
    }

    private static void clearSolution() {
        WorldRenderEventBus.unregister(PuzzlerSolver::onWorldRender);
        solution = null;
        tracePos = null;
    }

    public static void onWorldRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, WorldRenderContext context) {
        if (solution == null || tracePos == null) return;
        RenderLib.highlightBlock(context, solution, SOLUTION_COLOR, 0.5f, false);
        RenderLib.renderLineFromCursor(context, tracePos, SOLUTION_COLOR, 1.0f, 2);
    }

    public static void onAreaChange(String newArea, String oldArea) {
        clearSolution();
    }
}