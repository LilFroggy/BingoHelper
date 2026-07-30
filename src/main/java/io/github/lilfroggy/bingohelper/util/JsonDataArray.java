package io.github.lilfroggy.bingohelper.util;

import java.util.Iterator;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonDataArray implements Iterable<JsonElement> {
    
    protected JsonArray array;

    public JsonDataArray() {
        this.array = new JsonArray();
    }

    public JsonDataArray(JsonArray array) {
        this.array = array != null ? array : new JsonArray();
    }

    public <T> JsonDataArray add(T value) {
        switch (value) {
            case Boolean v -> array.add(v);
            case String v -> array.add(v);
            case Character v -> array.add(v);
            case Number v -> array.add(v);
            case JsonArray v -> array.add(v);
            case JsonObject v -> array.add(v);
            case JsonDataArray v -> array.add(v.array);
            case JsonDataObject v -> array.add(v.object);
            case null, default -> Logger.warn("Tried to add unsupported value to array: " + value);
        }
        return this;
    }

    @Nullable
    public String getString(int index) {
        JsonPrimitive primitive = getAsPrimitive(index);
        return (primitive != null && primitive.isString()) ? primitive.getAsString() : null;
    }

    @Nullable
    public Integer getInt(int index) {
        JsonPrimitive primitive = getAsPrimitive(index);
        return (primitive != null && primitive.isNumber()) ? primitive.getAsInt() : null;
    }

    @Nullable
    public Double getDouble(int index) {
        JsonPrimitive primitive = getAsPrimitive(index);
        return (primitive != null && primitive.isNumber()) ? primitive.getAsDouble() : null;
    }

    @Nullable
    public Float getFloat(int index) {
        JsonPrimitive primitive = getAsPrimitive(index);
        return (primitive != null && primitive.isNumber()) ? primitive.getAsFloat() : null;
    }

    @Nullable
    public Long getLong(int index) {
        JsonPrimitive primitive = getAsPrimitive(index);
        return (primitive != null && primitive.isNumber()) ? primitive.getAsLong() : null;
    }

    @Nullable
    public Boolean getBoolean(int index) {
        JsonPrimitive primitive = getAsPrimitive(index);
        return (primitive != null && primitive.isBoolean()) ? primitive.getAsBoolean() : null;
    }

    @Nullable
    public JsonDataArray getArray(int index) {
        JsonElement element = get(index);
        return (element != null && element.isJsonArray()) ? new JsonDataArray(element.getAsJsonArray()) : null;
    }

    @Nullable
    public JsonDataObject getObject(int index) {
        JsonElement element = get(index);
        return (element != null && element.isJsonObject()) ? new JsonDataObject(element.getAsJsonObject()) : null;
    }

    @Nullable
    public JsonElement get(int index) {
        if (index < 0 || index >= array.size()) return null;
        return array.get(index);
    }

    @Nullable
    private JsonPrimitive getAsPrimitive(int index) {
        JsonElement element = get(index);
        return (element != null && element.isJsonPrimitive()) ? element.getAsJsonPrimitive() : null;
    }

    public void clear() {
        array = new JsonArray();
    }

    public int size() {
        return array.size();
    }

    public boolean isEmpty() {
        return array.isEmpty();
    }

    @Override
    public Iterator<JsonElement> iterator() {
        return array.iterator();
    }

    public void forEachObject(Consumer<JsonDataObject> action) {
        for (JsonElement element : array) {
            if (element != null && element.isJsonObject()) {
                action.accept(new JsonDataObject(element.getAsJsonObject()));
            }
        }
    }

    public String toString() {
        return JsonUtils.toPretty(array);
    }
}