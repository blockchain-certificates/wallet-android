package com.hyland.android.app.data.cert.metadata;

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
            this.possibleJsonTypes.add(JsonType.valueOf(type.toUpperCase()));
        }
    }

    public List<JsonType> getPossibleJsonTypes() {
        return possibleJsonTypes;
    }
}
