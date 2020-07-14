package com.learningmachine.android.app.data.cert.metadata;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PropertyDef {
    public enum Format {
        @SerializedName("uri")
        URI,
        @SerializedName("email")
        EMAIL
    }

    public PropertyType type;
    public Format format;
    public String title;
    public String description;
    public String pattern;
    public boolean uniqueItems;
    @SerializedName("enum")
    public List<String> list;
}
