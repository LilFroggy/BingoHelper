package io.github.lilfroggy.bingohelper.util;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonDataObject {
    private final Map<String, JsonDataObject> cachedObjects = new HashMap<>();
    private final Map<String, JsonDataArray> cachedArrays = new HashMap<>();

    protected JsonObject object;

    public JsonDataObject() {
        this.object = new JsonObject();
    }

    public JsonDataObject(JsonObject object) {
        this.object = object != null ? object : new JsonObject();
    }

    public <T> JsonDataObject set(String key, T value) {
        switch (value) {
            case Boolean v -> object.addProperty(key, v);
            case String v -> object.addProperty(key, v);
            case Character v -> object.addProperty(key, v);
            case Number v -> object.addProperty(key, v);
            case JsonArray v -> object.add(key, v);
            case JsonObject v -> object.add(key, v);
            case JsonDataArray v -> object.add(key, v.array);
            case JsonDataObject v -> object.add(key, v.object);
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
    public JsonDataArray getArray(String key) {
        JsonElement element = object.get(key);
        if (element != null && element.isJsonArray()) {
            return cachedArrays.computeIfAbsent(key, k -> new JsonDataArray(element.getAsJsonArray()));
        }
        return null;
    }

    @Nullable
    public JsonDataObject getObject(String key) {
        JsonElement element = object.get(key);
        if (element != null && element.isJsonObject()) {
            return cachedObjects.computeIfAbsent(key, k -> new JsonDataObject(element.getAsJsonObject()));
        }
        return null;
    }

    public JsonDataArray getOrCreateArray(String key) {
        return cachedArrays.computeIfAbsent(key, k -> {
            JsonElement element = object.get(k);
            JsonArray arr = (element != null && element.isJsonArray()) ? element.getAsJsonArray() : null;
            
            if (arr == null) {
                arr = new JsonArray();
                object.add(k, arr);
            }
            return new JsonDataArray(arr);
        });
    }
    
    public JsonDataObject getOrCreateObject(String key) {
        return cachedObjects.computeIfAbsent(key, k -> {
            JsonElement element = object.get(k);
            JsonObject obj = (element != null && element.isJsonObject()) ? element.getAsJsonObject() : null;
            
            if (obj == null) {
                obj = new JsonObject();
                object.add(k, obj);
            }
            return new JsonDataObject(obj);
        });
    }
    
    @Nullable
    private JsonPrimitive getAsPrimitive(String key) {
        JsonElement element = object.get(key);
        return (element != null && element.isJsonPrimitive()) ? element.getAsJsonPrimitive() : null;
    }

    public String toString() {
        return JsonUtils.toPretty(object);
    }
}