package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

public class ImageSignature {
    @SerializedName("jobTitle")
    private String mJobTitle;

    public String getJobTitle() {
        return mJobTitle;
    }
}
