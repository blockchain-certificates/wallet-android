package com.learningmachine.android.app.data.cert.metadata;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

public class Metadata {

    private final List<String> mDisplayOrder;
    private final Map<String, JsonObject> mGroups;
    private final Map<String, GroupDef> mGroupDefinitions;
    private final List<Field> mFields;

    public Metadata(List<String> displayOrder, Map<String, JsonObject> groups, Map<String, GroupDef> groupDefinitions, List<Field> fields) {
        mDisplayOrder = displayOrder;
        mGroups = groups;
        mGroupDefinitions = groupDefinitions;
        mFields = fields;
    }

    public List<String> getDisplayOrder() {
        return mDisplayOrder;
    }

    public Map<String, JsonObject> getGroups() {
        return mGroups;
    }

    public Map<String, GroupDef> getGroupDefinitions() {
        return mGroupDefinitions;
    }

    public List<Field> getFields() {
        return mFields;
    }
}
