package io.github.lilfroggy.bingohelper.guide;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.util.Logger;

public class GuideSaver {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path SAVE_FILE = Paths.get("config", "bingohelper", "guide.json");

    public static void save(String guide) {
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            Files.writeString(SAVE_FILE, GSON.toJson(JsonParser.parseString(guide)));
        } catch (Exception e) {
            Logger.error("Failed to save guide", e);
        }
    }

    public static void saveProgress() {
        Config.savedIndex = Guide.stepIndex;
        Config.save();
    }
}