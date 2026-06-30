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

import io.github.lilfroggy.bingohelper.util.item.EnchantInfo;
import io.github.lilfroggy.bingohelper.util.item.EnchantList;

public class EnchantListAdapter implements JsonDeserializer<EnchantList> {
    @Override
    public EnchantList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, EnchantInfo> items = new LinkedHashMap<>();
        JsonObject obj = json.getAsJsonObject();
        Type listType = new TypeToken<List<String>>(){}.getType();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String id = entry.getKey();
            List<String> validEnchants = context.deserialize(entry.getValue(), listType);
            
            items.put(id, new EnchantInfo(id, validEnchants));
        }

        return new EnchantList(items);
    }
}