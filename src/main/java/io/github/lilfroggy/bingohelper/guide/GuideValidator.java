package io.github.lilfroggy.bingohelper.guide;

import com.networknt.schema.JsonSchema;

import io.github.lilfroggy.bingohelper.util.JsonValidator;

public class GuideValidator {
    private static JsonSchema GUIDE_SCHEMA;

    static {
        GUIDE_SCHEMA = JsonValidator.getJsonSchema("/guide-schema.json");
    }

    public static boolean isValidGuide(String guide) {
        return JsonValidator.isValidJson(guide, GUIDE_SCHEMA);
    }
}