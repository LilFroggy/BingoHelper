package io.github.lilfroggy.bingohelper.guide.deserializing;

import java.lang.reflect.Type;
import net.minecraft.world.phys.Vec3;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Vec3dAdapter implements JsonDeserializer<Vec3> {
    @Override
    public Vec3 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonArray array = json.getAsJsonArray();
        
        double x = array.get(0).getAsDouble();
        double y = array.get(1).getAsDouble();
        double z = array.get(2).getAsDouble();
        
        return new Vec3(x, y, z);
    }
}