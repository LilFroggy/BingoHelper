package io.github.lilfroggy.bingohelper.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import io.github.lilfroggy.bingohelper.config.Config;

import java.io.StringWriter;
import java.io.IOException;

public class JsonUtils {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static String toPretty(JsonElement json) {
        return toPretty(json, Config.jsonIndent);
    }

    public static String toPretty(JsonElement json, String indent) {
        try (StringWriter stringWriter = new StringWriter();
             JsonWriter jsonWriter = new JsonWriter(stringWriter)) {
            
            jsonWriter.setIndent(indent.translateEscapes());
            GSON.toJson(json, jsonWriter);
            
            return stringWriter.toString();
        } catch (IOException e) {
            Logger.error("Failed to serialize JSON", e);
            return GSON.toJson(json);
        }
    }
}