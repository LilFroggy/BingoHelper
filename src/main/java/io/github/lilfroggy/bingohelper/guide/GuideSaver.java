package io.github.lilfroggy.bingohelper.guide;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.util.Logger;

public class GuideSaver {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path LATEST_SAVE_PATH = Paths.get("config", "bingohelper", "guides", "latest.json");
    public static final Path ACTIVE_SAVE_PATH = Paths.get("config", "bingohelper", "guides", "active.json");

    public static void saveActiveGuide(String guide) {
        saveGuide(guide, ACTIVE_SAVE_PATH);
    }

    public static void saveLatestGuide(String guide) {
        saveGuide(guide, LATEST_SAVE_PATH);
    }

    private static void saveGuide(String guide, Path path) {
        CompletableFuture.runAsync(() -> {
            try {
                Files.createDirectories(path.getParent());
                Files.writeString(path, GSON.toJson(JsonParser.parseString(guide)));
            } catch (Exception e) {
                Logger.error("Failed to save guide to " + path, e);
            }
        });
    }

    public static void saveUserProgress() {
        Config.savedIndex = Guide.stepIndex;
        Config.save();
    }
}