package io.github.lilfroggy.bingohelper.guide;

import io.github.lilfroggy.bingohelper.guide.step.Step;

public record GuideData(String name, int version, int stepIndex, Step[] steps, String raw) {}