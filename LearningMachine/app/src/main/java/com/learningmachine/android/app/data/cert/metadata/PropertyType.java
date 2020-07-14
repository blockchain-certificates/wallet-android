package com.learningmachine.android.app.data.cert.metadata;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PropertyType {
    public enum JsonType {
        @SerializedName("string")
        STRING,
        @SerializedName("number")
        NUMBER,
        @SerializedName("integer")
        INTEGER,
        @SerializedName("array")
        ARRAY,
        @SerializedName("boolean")
        BOOLEAN,
        @SerializedName("object")
        OBJECT,
        @SerializedName("null")
        NULL
    }

    private List<JsonType> possibleJsonTypes;

    public PropertyType(List<String> types) {
        this.possibleJsonTypes = new ArrayList<>();
        for (String type : types) {
            switch (type) {
                case "string":
                    this.possibleJsonTypes.add(JsonType.STRING);
                    break;
                case "number":
                    this.possibleJsonTypes.add(JsonType.NUMBER);
                    break;
                case "integer":
                    this.possibleJsonTypes.add(JsonType.INTEGER);
                    break;
                case "array":
                    this.possibleJsonTypes.add(JsonType.ARRAY);
                    break;
                case "boolean":
                    this.possibleJsonTypes.add(JsonType.BOOLEAN);
                    break;
                case "object":
                    this.possibleJsonTypes.add(JsonType.OBJECT);
                    break;
                case "null":
                    this.possibleJsonTypes.add(JsonType.NULL);
                    break;
            }
        }
    }

    public List<JsonType> getPossibleJsonTypes() {
        return possibleJsonTypes;
    }
}
