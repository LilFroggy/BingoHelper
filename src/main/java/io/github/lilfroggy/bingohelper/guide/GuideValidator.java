package io.github.lilfroggy.bingohelper.guide;

import com.networknt.schema.JsonSchema;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.util.JsonValidator;

public class GuideValidator {
    private static final JsonSchema GUIDE_SCHEMA = JsonValidator.getJsonSchema("/guide-schema.json");

    public static boolean isValidGuide(String guide) {
        if (!Config.validateGuides) return true;
        return JsonValidator.isValidJson(guide, GUIDE_SCHEMA);
    }
}