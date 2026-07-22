package io.github.lilfroggy.bingohelper.util;

import com.google.gson.JsonParser;

public class PersistentData extends JsonDataObject {

    final String path;
    final String defaultValue;

    public PersistentData(String path, String defaultValue) {
        super(JsonParser.parseString(defaultValue).getAsJsonObject());
        this.path = path;
        this.defaultValue = defaultValue;
        load();
    }

    public void load() {
        String content = FileLib.read(path, defaultValue);
        json = JsonParser.parseString(content).getAsJsonObject();
    }

    public void save() {
        FileLib.write(path, JsonUtils.toPretty(json));
    }
}