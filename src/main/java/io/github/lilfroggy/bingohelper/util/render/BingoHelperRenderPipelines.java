package io.github.lilfroggy.bingohelper.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;

import io.github.lilfroggy.bingohelper.BingoHelper;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;

// Shoutout Skyblocker

public class BingoHelperRenderPipelines {
    public static final RenderPipeline LINES_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
        .withLocation(BingoHelper.id("pipeline/lines_through_walls"))
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build());

    static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
        .withLocation(BingoHelper.id("pipeline/debug_filled_box_through_walls"))
        .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build());
        
    public static void init() {}
}