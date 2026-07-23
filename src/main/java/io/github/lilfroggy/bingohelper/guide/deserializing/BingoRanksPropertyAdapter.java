package io.github.lilfroggy.bingohelper.guide.deserializing;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import io.github.lilfroggy.bingohelper.guide.step.properties.bingoRanks.BingoRanksProperty;

public class BingoRanksPropertyAdapter implements JsonDeserializer<BingoRanksProperty> {
    @Override
    public BingoRanksProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        
        BingoRanksProperty property = new BingoRanksProperty();
        
        property.ranks = context.deserialize(json, new TypeToken<List<Integer>>(){}.getType());

        return property;
    }
}