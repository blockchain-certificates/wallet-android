package com.learningmachine.android.app.data.web;

import com.google.gson.annotations.SerializedName;

public class KeyRotation {
    @SerializedName("date")
    private String date;
    @SerializedName("key")
    private String key;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
