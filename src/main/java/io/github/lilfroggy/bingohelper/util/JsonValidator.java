package io.github.lilfroggy.bingohelper.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.SpecVersion;

import java.io.InputStream;
import java.util.Set;

public class JsonValidator {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    public static boolean isValidJson(String json, JsonSchema schema) {
        try {
            JsonNode jsonNode = MAPPER.readTree(json);
            Set<ValidationMessage> errors = schema.validate(jsonNode);
            if (errors.isEmpty()) return true;
            else throw new Exception(errors.toString());
        } catch (Exception e) {
            Logger.error("Invalid JSON", e);
            return false;
        }
    }

    public static JsonSchema getJsonSchema(String path) {
        try {
            InputStream schemaStream = JsonValidator.class.getResourceAsStream(path);
            return FACTORY.getSchema(schemaStream);
        } catch (Exception e) {
            Logger.error("Error getting schema", e);
            return null;
        }
    }
}