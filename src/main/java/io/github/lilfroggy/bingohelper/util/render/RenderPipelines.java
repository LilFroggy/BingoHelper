package io.github.lilfroggy.bingohelper.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;

import io.github.lilfroggy.bingohelper.BingoHelper;
import net.minecraft.client.render.VertexFormats;

public class RenderPipelines {
    public static final RenderPipeline FILLED_PIPELINE = net.minecraft.client.gl.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.gl.RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation(BingoHelper.id("filled"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
            .build());

    public static final RenderPipeline FILLED_PIPELINE_NO_DEPTH = net.minecraft.client.gl.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.gl.RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation(BingoHelper.id("through-walls-filled"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build());

    public static final RenderPipeline FILLED_ENTITY_PIPELINE = net.minecraft.client.gl.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.gl.RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation(BingoHelper.id("filled-entity"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
            .withDepthWrite(true)
            .build());

    public static final RenderPipeline OUTLINE_ENTITY_PIPELINE = net.minecraft.client.gl.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.gl.RenderPipelines.RENDERTYPE_LINES_SNIPPET)
            .withLocation(BingoHelper.id("outline-entity"))
            .withDepthWrite(true)
            .build());

    public static final RenderPipeline OUTLINE_ENTITY_PIPELINE_NO_DEPTH = net.minecraft.client.gl.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.gl.RenderPipelines.RENDERTYPE_LINES_SNIPPET)
            .withLocation(BingoHelper.id("outline-entity-no-depth"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build());

}