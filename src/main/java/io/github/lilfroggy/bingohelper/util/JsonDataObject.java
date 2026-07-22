package io.github.lilfroggy.bingohelper.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonDataObject {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    protected JsonObject json;
    private final Map<String, JsonDataObject> cached = new HashMap<>();

    public JsonDataObject() {
        this.json = new JsonObject();
    }

    public JsonDataObject(JsonObject json) {
        this.json = json != null ? json : new JsonObject();
    }

    public <T> JsonDataObject set(String key, T value) {
        switch (value) {
            case Boolean v -> json.addProperty(key, v);
            case String v -> json.addProperty(key, v);
            case Character v -> json.addProperty(key, v);
            case Number v -> json.addProperty(key, v);
            case JsonArray v -> json.add(key, v);
            case JsonObject v -> json.add(key, v);
            case JsonDataObject v -> json.add(key, v.json);
            case null, default -> Logger.warn("Tried to set " + key + " to " + value);
        }
        return this;
    }

    @Nullable
    public String getString(String key) {
        JsonPrimitive primitive = getAsPrimitive(key);
        return (primitive != null && primitive.isString()) ? primitive.getAsString() : null;
    }

    @Nullable
    public Integer getInt(String key) {
        JsonPrimitive primitive = getAsPrimitive(key);
        return (primitive != null && primitive.isNumber()) ? primitive.getAsInt() : null;
    }

    @Nullable
    public Double getDouble(String key) {
        JsonPrimitive primitive = getAsPrimitive(key);
        return (primitive != null && primitive.isNumber()) ? primitive.getAsDouble() : null;
    }

    @Nullable
    public Float getFloat(String key) {
        JsonPrimitive primitive = getAsPrimitive(key);
        return (primitive != null && primitive.isNumber()) ? primitive.getAsFloat() : null;
    }

    @Nullable
    public Long getLong(String key) {
        JsonPrimitive primitive = getAsPrimitive(key);
        return (primitive != null && primitive.isNumber()) ? primitive.getAsLong() : null;
    }

    @Nullable
    public Boolean getBoolean(String key) {
        JsonPrimitive primitive = getAsPrimitive(key);
        return (primitive != null && primitive.isBoolean()) ? primitive.getAsBoolean() : null;
    }

    @Nullable
    public List<?> getList(String key) {
        JsonElement element = json.get(key);
        return (element != null && element.isJsonArray()) ? element.getAsJsonArray().asList() : null;
    }

    @Nullable
    public Map<?, ?> getMap(String key) {
        JsonElement element = json.get(key);
        return (element != null && element.isJsonObject()) ? element.getAsJsonObject().asMap() : null;
    }

    @Nullable
    public JsonDataObject getObject(String key) {
        JsonElement element = json.get(key);
        if (element != null && element.isJsonObject()) {
            return cached.computeIfAbsent(key, k -> new JsonDataObject(element.getAsJsonObject()));
        }
        return null;
    }
    
    public JsonDataObject getOrCreateObject(String key) {
        return cached.computeIfAbsent(key, k -> {
            JsonElement element = json.get(k);
            JsonObject obj = (element != null && element.isJsonObject()) ? element.getAsJsonObject() : null;
            
            if (obj == null) {
                obj = new JsonObject();
                json.add(k, obj);
            }
            return new JsonDataObject(obj);
        });
    }
    
    @Nullable
    private JsonPrimitive getAsPrimitive(String key) {
        JsonElement element = json.get(key);
        return (element != null && element.isJsonPrimitive()) ? element.getAsJsonPrimitive() : null;
    }

    public String toString() {
        return GSON.toJson(json);
    }
}