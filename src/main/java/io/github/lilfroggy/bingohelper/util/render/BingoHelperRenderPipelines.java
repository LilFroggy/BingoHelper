package io.github.lilfroggy.bingohelper.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class BingoHelperRenderPipelines {
    /**
     * RenderPipeline for lines through walls (no depth test)
     */
    public static final RenderPipeline LINES_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
        .withLocation(Identifier.of("bingohelper", "pipeline/lines_through_walls"))
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build());

    static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
        .withLocation(Identifier.of("bingohelper", "pipeline/debug_filled_box_through_walls"))
        .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build());

    /**
     * Call this to ensure pipelines are pre-compiled instead of compiled on demand
     */
    public static void init() {}
}