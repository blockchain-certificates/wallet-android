package com.learningmachine.android.app.data.webservice.response;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public class KeyRotationResponse {

    @SerializedName("date")
    private String mCreatedDate;
    @SerializedName("key")
    private String mKey;

    public KeyRotationResponse(String createdDate, String key) {
        mCreatedDate = createdDate;
        mKey = key;
    }

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public DateTime getCreatedDateTime() {
        return DateTime.parse(mCreatedDate);
    }

    public String getKey() {
        return mKey;
    }
}
