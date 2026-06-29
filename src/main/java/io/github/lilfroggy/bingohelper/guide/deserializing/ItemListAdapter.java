package io.github.lilfroggy.bingohelper.guide.deserializing;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.github.lilfroggy.bingohelper.util.item.HasList;
import io.github.lilfroggy.bingohelper.util.item.HasInfo;

public class ItemListAdapter implements JsonDeserializer<HasList> {
    @Override
    public HasList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, HasInfo> items = new HashMap<>();
        JsonObject obj = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String id = entry.getKey();
            int target = entry.getValue().getAsInt();

            items.put(id, new HasInfo(id, target));
        }

        return new HasList(items);
    }
}