package io.github.lilfroggy.bingohelper.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PersistentData {

    final String path;
    final String defaultValue;
    private JsonObject currentData;

    public PersistentData(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
        load();
    }

    public JsonObject get() {
        return currentData;
    }

    public void load() {
        String content = FileLib.read(path, defaultValue);
        currentData = JsonParser.parseString(content).getAsJsonObject();
    }

    public void save() {
        FileLib.write(path, JsonUtils.toPretty(currentData));
    }
}