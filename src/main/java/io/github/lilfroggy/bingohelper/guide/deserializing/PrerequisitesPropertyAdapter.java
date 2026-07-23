package io.github.lilfroggy.bingohelper.guide.deserializing;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.guide.step.properties.prerequisites.PrerequisitesProperty;

public class PrerequisitesPropertyAdapter implements JsonDeserializer<PrerequisitesProperty> {
    @Override
    public PrerequisitesProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        PrerequisitesProperty property = new PrerequisitesProperty();

        property.steps = json.isJsonNull() ? new Step[0] : context.deserialize(json, Step[].class);

        return property;
    }
}