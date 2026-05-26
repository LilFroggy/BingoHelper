package io.github.lilfroggy.bingohelper.guide;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.lilfroggy.bingohelper.guide.deserializing.StepDeserializer;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Logger;

public class GuideParser {
    
    @Nullable
    public static GuideData toGuideData(String guide) {
        return toGuideData(toJsonObject(guide));
    }

    @Nullable
    private static JsonObject toJsonObject(String guide) {
        try {
            if (!GuideValidator.isValidGuide(guide)) return null;
            JsonObject guideObject = JsonParser.parseString(guide).getAsJsonObject();
            return guideObject;
        } catch (Exception e) {
            Logger.error("Error parsing guide", e);
            String received = guide == null ? guide : guide.substring(0, Math.min(200, guide.length())) + "...";
            Logger.info("Guide received: " + received, true);
            return null;
        }
    }

    @Nullable
    private static GuideData toGuideData(JsonObject guide) {
        if (guide == null) return null;
        String name = guide.get("name").getAsString();
        int version = guide.get("version").getAsInt();
        int stepIndex = guide.getAsJsonObject("data").get("stepIndex").getAsInt();
        Step[] steps = stepsFromJsonArray(guide.getAsJsonObject("data").getAsJsonArray("steps"));
        return new GuideData(name, version, stepIndex, steps, guide.toString());
    }

    private static Step[] stepsFromJsonArray(JsonArray jsonArray) {
        Step[] steps = jsonArray.asList().stream()
            .map(element -> StepDeserializer.stepFromJson(element.toString()))
            .toArray(Step[]::new);

        for (int i = 0; i < steps.length; i++) {
            steps[i].setIndex(i);
        }

        return steps;
    }
}