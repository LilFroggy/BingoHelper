package io.github.lilfroggy.bingohelper.guide.deserializing;

import net.minecraft.util.math.Vec3d;
import java.lang.reflect.Type;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Vec3dAdapter implements JsonDeserializer<Vec3d> {
    @Override
    public Vec3d deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonArray array = json.getAsJsonArray();
        
        double x = array.get(0).getAsDouble();
        double y = array.get(1).getAsDouble();
        double z = array.get(2).getAsDouble();
        
        return new Vec3d(x, y, z);
    }
}