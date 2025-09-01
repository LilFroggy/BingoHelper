package io.github.lilfroggy.bingohelper.guide;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.lilfroggy.bingohelper.guide.steps.Step;
import io.github.lilfroggy.bingohelper.util.Logger;

public class GuideParser {
    /**
     * Parses a JSON string into a {@link GuideData} object.
     * @param guide The JSON string representing the guide.
     * @return A {@link GuideData} object if the string represents a valid guide, or {@code null} if the JSON is invalid or does not meet the guide's format requirements.
     */
    public static GuideData toGuideData(String guide) {
        return toGuideData(toJsonObject(guide));
    }

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

    private static GuideData toGuideData(JsonObject guide) {
        if (guide == null) return null;
        String name = guide.get("name").getAsString();
        int version = guide.get("version").getAsInt();
        int stepIndex = guide.getAsJsonObject("data").get("stepIndex").getAsInt();
        Step[] steps = stepsFromJsonArray(guide.getAsJsonObject("data").getAsJsonArray("steps"));
        return new GuideData(name, version, stepIndex, steps, guide.toString());
    }

    private static Step[] stepsFromJsonArray(JsonArray steps) {
        return steps.asList().stream().map(step -> StepParser.stepFromJson(step.toString())).toArray(Step[]::new);
    }
}