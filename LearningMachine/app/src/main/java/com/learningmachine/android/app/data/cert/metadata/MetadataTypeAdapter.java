package com.learningmachine.android.app.data.cert.metadata;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MetadataTypeAdapter implements JsonDeserializer<Metadata> {
    private NumberFormat mNumberFormat;
    private NumberFormat mIntegerFormat;
    private String mTrueString;
    private String mFalseString;

    public MetadataTypeAdapter(NumberFormat numberFormat, NumberFormat integerFormat, String trueString, String falseString) {
        mNumberFormat = numberFormat;
        mIntegerFormat = integerFormat;
        mTrueString = trueString;
        mFalseString = falseString;
    }

    @Override
    public Metadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        List<String> displayOrder = getDisplayOrder(jsonObject);
        Map<String, JsonObject> groups = getGroups(jsonObject);
        Map<String, GroupDef> groupDefinitions = getGroupDefinitions(jsonObject, context);
        List<Field> fields = getFields(displayOrder, groups, groupDefinitions);

        return new Metadata(displayOrder, groups, groupDefinitions, fields);
    }

    private List<String> getDisplayOrder(JsonObject jsonObject) {
        List<String> displayOrder = new ArrayList<>();
        for (JsonElement item : jsonObject.get("displayOrder").getAsJsonArray()) {
            displayOrder.add(item.getAsString());
        }
        return displayOrder;
    }

    private Map<String, GroupDef> getGroupDefinitions(JsonObject jsonObject, JsonDeserializationContext context) {
        Map<String, GroupDef> groupDefinitions = new HashMap<>();
        JsonObject schema = jsonObject.get("schema").getAsJsonObject();
        JsonObject schemaProperties = schema.get("properties").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : schemaProperties.entrySet()) {
            if (!entry.getValue().getAsJsonObject().has("properties")) {
                continue;
            }
            GroupDef groupDef = new GroupDef();
            groupDef.key = entry.getKey();
            groupDef.properties = new HashMap<>();
            JsonObject groupProperties = entry.getValue().getAsJsonObject().get("properties").getAsJsonObject();
            for (Map.Entry<String, JsonElement> propertyEntry : groupProperties.entrySet()) {
                PropertyDef propertyDef = context.deserialize(propertyEntry.getValue(), PropertyDef.class);
                groupDef.properties.put(propertyEntry.getKey(), propertyDef);
            }
            groupDefinitions.put(groupDef.key, groupDef);
        }
        return groupDefinitions;
    }

    private Map<String, JsonObject> getGroups(JsonObject jsonObject) {
        Map<String, JsonObject> groups = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String entryKey = entry.getKey();
            if (entryKey.equals("schema") || entryKey.equals("displayOrder")) {
                continue;
            }
            groups.put(entryKey, entry.getValue().getAsJsonObject());
        }
        return groups;
    }

    private List<Field> getFields(List<String> displayOrder, Map<String, JsonObject> groups, Map<String, GroupDef> groupDefinitions) {
        List<Field> fields = new ArrayList<>();
        for (String fieldRef : displayOrder) {
            String[] split = fieldRef.split("\\.");
            if (split.length != 2) {
                continue;
            }
            String groupName = split[0];
            String fieldName = split[1];
            JsonObject group = groups.get(groupName);
            GroupDef groupDef = groupDefinitions.get(groupName);
            PropertyDef propertyDef = groupDef.properties.get(fieldName);
            JsonElement fieldValueElement = group.get(fieldName);
            String value = null;
            switch (propertyDef.type) {
                case STRING:
                    value = fieldValueElement.getAsString();
                    break;
                case NUMBER:
                    value = mNumberFormat.format(fieldValueElement.getAsDouble());
                    break;
                case INTEGER:
                    value = mIntegerFormat.format(fieldValueElement.getAsInt());
                    break;
                case ARRAY:
                    // This is the same algorithm as TextUtils.join and StringUtils.join
                    StringBuilder sb = new StringBuilder();
                    Iterator<JsonElement> it = fieldValueElement.getAsJsonArray().iterator();
                    if (it.hasNext()) {
                        sb.append(it.next().toString());
                        while (it.hasNext()) {
                            sb.append(", ");
                            sb.append(it.next().toString());
                        }
                    }
                    value = sb.toString();
                break;
                case BOOLEAN:
                    value = fieldValueElement.getAsBoolean() ? mTrueString : mFalseString;
                    break;
            }
            Field field = new Field(propertyDef.title, value);
            fields.add(field);
        }
        return fields;
    }
}
