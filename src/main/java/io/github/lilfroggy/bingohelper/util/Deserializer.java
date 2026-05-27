package io.github.lilfroggy.bingohelper.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Deserializer<T> implements JsonDeserializer<T> {
    private final String typeFieldName;
    private final Map<String, Class<? extends T>> registry = new HashMap<>();

    public Deserializer(String typeFieldName) {
        this.typeFieldName = typeFieldName;
    }

    public Deserializer<T> register(String label, Class<? extends T> subtype) {
        registry.put(label, subtype);
        return this;
    }

    @Override
    @SuppressWarnings("null")
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        if (!jsonObject.has(typeFieldName)) {
            throw new JsonParseException("Missing type field: " + typeFieldName);
        }
        
        String typeLabel = jsonObject.get(typeFieldName).getAsString();
        Class<? extends T> targetClass = registry.get(typeLabel);
        
        if (targetClass == null) {
            Logger.warn("Unknown type label: " + typeLabel);
            return null;
        }
        
        return context.deserialize(jsonObject, targetClass);
    }
}