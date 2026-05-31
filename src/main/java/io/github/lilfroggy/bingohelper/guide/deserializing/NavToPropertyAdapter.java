package io.github.lilfroggy.bingohelper.guide.deserializing;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import io.github.lilfroggy.bingohelper.guide.step.properties.navTo.NavToProperty;

public class NavToPropertyAdapter implements JsonDeserializer<NavToProperty> {
    @Override
    public NavToProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        NavToProperty property = new NavToProperty();
        
        property.navTo = json.isJsonNull() ? null : json.getAsString();

        return property;
    }
}