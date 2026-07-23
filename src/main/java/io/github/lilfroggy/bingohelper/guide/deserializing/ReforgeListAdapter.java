package io.github.lilfroggy.bingohelper.guide.deserializing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import io.github.lilfroggy.bingohelper.util.item.ReforgeInfo;
import io.github.lilfroggy.bingohelper.util.item.ReforgeList;

public class ReforgeListAdapter implements JsonDeserializer<ReforgeList> {
    @Override
    public ReforgeList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Map<String, ReforgeInfo> items = new LinkedHashMap<>();
        JsonObject obj = json.getAsJsonObject();

        for (var entry : obj.entrySet()) {
            String id = entry.getKey();
            List<String> validReforges = context.deserialize(entry.getValue(), new TypeToken<List<String>>(){}.getType());
            
            items.put(id, new ReforgeInfo(validReforges));
        }

        return new ReforgeList(items);
    }
}