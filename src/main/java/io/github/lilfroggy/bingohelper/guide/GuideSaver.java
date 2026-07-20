package io.github.lilfroggy.bingohelper.guide;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.util.FileLib;

public class GuideSaver {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String LATEST_SAVE_PATH = "config/bingohelper/guides/latest.json";
    public static final String ACTIVE_SAVE_PATH = "config/bingohelper/guides/active.json";

    public static void saveActiveGuide(String guide) {
        saveGuide(guide, ACTIVE_SAVE_PATH);
    }

    public static void saveLatestGuide(String guide) {
        saveGuide(guide, LATEST_SAVE_PATH);
    }

    private static void saveGuide(String guide, String path) {
        FileLib.write(path, GSON.toJson(JsonParser.parseString(guide)));
    }

    public static void saveUserProgress() {
        Config.savedIndex = Guide.stepIndex;
        ActiveSteps.save();
    }
}