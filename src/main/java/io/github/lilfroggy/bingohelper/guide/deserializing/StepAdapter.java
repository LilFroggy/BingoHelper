package io.github.lilfroggy.bingohelper.guide.deserializing;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.github.lilfroggy.bingohelper.util.Logger;

public class StepAdapter<T> implements JsonDeserializer<T> {
    private final String typeFieldName;
    private final Map<String, Class<? extends T>> registry = new HashMap<>();

    public StepAdapter(String typeFieldName) {
        this.typeFieldName = typeFieldName;
    }

    public StepAdapter<T> register(String label, Class<? extends T> subtype) {
        registry.put(label, subtype);
        return this;
    }

    @Override
    @SuppressWarnings("null")
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            
            if (!jsonObject.has(typeFieldName)) {
                throw new JsonParseException("Missing type field: " + typeFieldName);
            }
            
            String type = jsonObject.get(typeFieldName).getAsString();
            Class<? extends T> implClass = registry.get(type);
            
            if (implClass == null) {
                Logger.warn("Unknown step type: " + type);
                return null;
            }
            
            T instance = implClass.getDeclaredConstructor().newInstance();
            
            for (var entry : jsonObject.entrySet()) {
                String fieldName = entry.getKey();
                JsonElement value = entry.getValue();

                Field field = null;
                try {
                    field = implClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e1) {
                    try {
                        Class<?> abstractClass = implClass.getSuperclass();
                            field = abstractClass.getDeclaredField(fieldName);
                    } catch (NoSuchFieldException e2) {
                        continue;
                    }
                }

                try {
                    field.setAccessible(true);
                    field.set(instance, context.deserialize(value, field.getGenericType()));
                } catch (Exception e) {
                    Logger.error("Failed to set field '" + fieldName + "' on step type: " + type, e);
                }
            }

            return instance;
        } catch (Exception e) {
            Logger.error("Failed to parse step: " + json, e);
            return null;
        }
    }
}