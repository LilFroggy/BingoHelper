package io.github.lilfroggy.bingohelper.util.render;

import io.github.lilfroggy.bingohelper.BingoHelper;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

public class RenderPipelines {
    public static final RenderPipeline FILLED_DEBUG = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(BingoHelper.id("filled-debug-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build());

    public static final RenderPipeline FILLED_DEBUG_NO_DEPTH = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(BingoHelper.id("filled-debug-no-depth-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .build());



    public static final RenderPipeline FILLED = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(BingoHelper.id("filled-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build());

    public static final RenderPipeline LINE = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.LINES_SNIPPET)
            .withLocation(BingoHelper.id("line-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build());


    public static final RenderPipeline FILLED_NO_DEPTH = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(BingoHelper.id("filled-no-depth-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build());


    public static final RenderPipeline LINE_NO_DEPTH = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.LINES_SNIPPET)
            .withLocation(BingoHelper.id("line-no-depth-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build());

}