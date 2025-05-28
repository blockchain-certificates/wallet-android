package com.hyland.android.app.data.cert.metadata;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PropertyTypeTypeAdapter implements JsonDeserializer<PropertyType> {
    @Override
    public PropertyType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<String> types = new ArrayList<>();
        if (json.isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray()) {
                types.add(element.getAsString());
            }
        } else {
            types.add(json.getAsString());
        }
        return new PropertyType(types);
    }
}
