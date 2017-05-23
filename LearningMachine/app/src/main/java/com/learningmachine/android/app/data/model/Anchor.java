package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

public class Anchor {
    @SerializedName("sourceId")
    private String mSourceId;
    @SerializedName("type")
    private String mType;

    public String getSourceId() {
        return mSourceId;
    }
}
